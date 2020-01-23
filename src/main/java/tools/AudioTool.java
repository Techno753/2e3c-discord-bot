package tools;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import tools.audio.MyAudioLoadResultHandler;
import tools.audio.TrackScheduler;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public final class AudioTool {
    private static ArrayList<ConnectionData> connections = new ArrayList<>();

    // Channel methods
    public static int connectToVC(GuildMessageReceivedEvent gmre) {
        AudioManager am;

        // Connect to channel
        if ((am = gmre.getGuild().getAudioManager()) == null) {
            return -1; // User is not in a vc
        }
        VoiceChannel vc = gmre.getMember().getVoiceState().getChannel();
        am.openAudioConnection(vc);

        // Register sources
        AudioPlayerManager apm = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerLocalSource(apm);
        AudioSourceManagers.registerRemoteSources(apm);

        // Create and set AudioPlayer from AudioPlayerManager
        AudioPlayer player = apm.createPlayer();
        AudioPlayerSendHandler apsh = new AudioPlayerSendHandler(player);
        am.setSendingHandler(apsh);

        // Create and set TrackScheduler to get events from AudioPlayer
        TrackScheduler ts = new TrackScheduler(player);
        ts.setGuild(gmre.getGuild());
        player.addListener(ts);
        MyAudioLoadResultHandler audioLoader = new MyAudioLoadResultHandler(ts);

        connections.add(new ConnectionData(gmre.getGuild().getId(), apm, player, audioLoader, ts));
        return 1;
    }

    public static int connect(GuildMessageReceivedEvent gmre) {
        // Get serverID
        String serverID = gmre.getGuild().getId();

        // if user not in vc then reply message
        if (gmre.getMember().getVoiceState().getChannel() == null) {
            return -1;

        // if bot not in any channel then connect to user's channel
        } else if (!AudioTool.exists(serverID)) {
            AudioTool.connectToVC(gmre);

        // if bot in incorrect channel then move
        } else if (!gmre.getGuild().getAudioManager().getConnectedChannel().getId()
                .equals(gmre.getMember().getVoiceState().getChannel().getId())) {
            gmre.getGuild().getAudioManager().getConnectedChannel().getId();
            AudioTool.updateChannel(gmre);
        }
        return 1;
    }

    public static void updateChannel(GuildMessageReceivedEvent gmre) {
        System.out.println("Moving channels...");
        AudioManager am = gmre.getGuild().getAudioManager();
        VoiceChannel vc = gmre.getMember().getVoiceState().getChannel();

        // pause player and close connection
        getServerAP(gmre.getGuild().getId()).setPaused(true);
        am.closeAudioConnection();

        // wait 2 seconds
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // reconnect to new channel and resume playing
        am.openAudioConnection(vc);
        getServerAP(gmre.getGuild().getId()).setPaused(false);
    }

    public static void disconnectFromVC(Guild g) {
        ConnectionData toRemove = null;

        for (ConnectionData cd : connections) {
            if (cd.getServerID().equals(g.getId())) {
                // close connection to channel
                g.getAudioManager().closeAudioConnection();
                // clear tracks
                cd.getTS().clear();
                // destroys player
                cd.getAP().destroy();
                // shutdown APM
                cd.getAPM().shutdown();
                // set ConnectionData to remove
                toRemove = cd;
            }
        }
        if (toRemove != null) {
            connections.remove(toRemove);
        }

        // Close channel connection
        g.getAudioManager().closeAudioConnection();
    }

    // Playback methods
    // Queues song
    public static String[] queue(String s, GuildMessageReceivedEvent gmre) {
        // Get serverID
        String serverID = gmre.getGuild().getId();

        // Queue song
        try {
            getServerAPM(serverID).loadItem(s, AudioTool.getServerALRH(serverID)).get();
            TrackScheduler ts = getServerTS(gmre.getGuild().getId());
            return new String[] {ts.getQueuedTitle(), ts.getQueuedType()};
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int pausePlayback(GuildMessageReceivedEvent gmre) {
        String serverID = gmre.getGuild().getId();

        if (exists(serverID)) {
            AudioPlayer ap = getServerAP(serverID);
            if (ap.getPlayingTrack() != null) {
                if (!ap.isPaused()) {
                    ap.setPaused(true);
                    return 1;   // paused
                }
                return -3; // already paused
            }
            return -2; // not playing
        }
        return -1; // not connected
    }

    public static int resumePlayback(GuildMessageReceivedEvent gmre) {
        String serverID = gmre.getGuild().getId();
        if (exists(serverID)) {
            AudioPlayer ap = getServerAP(serverID);
            if (ap.getPlayingTrack() != null) {
                if (ap.isPaused()) {
                    ap.setPaused(false);
                    return 1;   // resumed
                }
                return -3; // already playing
            }
            return -2; // not playing
        }
        return -1; // not connected
    }

    public static int skip(GuildMessageReceivedEvent gmre) {
        AudioPlayer ap;
        String serverID = gmre.getGuild().getId();

        // check connected
        if (exists(serverID)) {
            // check if playing
            if ((ap = getServerAP(serverID)).getPlayingTrack() != null) {
                    // stop current and play next
                    ap.stopTrack();
                    getServerTS(serverID).nextTrack();
                    return 1;
            }
            return -2; // not playing
        }
        return -1; // not connected
    }

    public static int skipN(GuildMessageReceivedEvent gmre, int n) {
        AudioPlayer ap;
        TrackScheduler ts;
        String serverID = gmre.getGuild().getId();

        // check connected
        if (exists(serverID)) {
            // check if playing
            if ((ap = getServerAP(serverID)).getPlayingTrack() != null) {
                // stop current and play next
                ap.stopTrack();
                ts = getServerTS(serverID);
                for (int i = 0; i < (n-1); i++) {
                    ts.skip();
                }
                ts.nextTrack();
                return 1;
            }
            return -2; // not playing
        }
        return -1; // not connected
    }

    public static int clear(GuildMessageReceivedEvent gmre) {
        String serverID = gmre.getGuild().getId();

        // check connected
        if (exists(serverID)) {
                // stop current and play next TODO
                int size = getServerTS(serverID).getQueue().size();
                skipN(gmre, size);
                getServerAP(gmre.getGuild().getId()).stopTrack();
                return 1;
        }
        return -1; // not connected
    }

    public static String getStatusString(GuildMessageReceivedEvent gmre) {
        AudioPlayer ap = getServerAP(gmre.getGuild().getId());
        AudioTrack at;

        if ((at = ap.getPlayingTrack()) != null) {

            // Get parts
            String title = at.getInfo().title;

            // get time strings
            String durString = String.valueOf(at.getDuration());
            durString = durString.substring(0, durString.length()-3);
            long durLong = Long.parseLong(durString);
            String durTime = TimeTool.secToString(durLong);

            String posString = String.valueOf(at.getPosition());
            posString = posString.substring(0, posString.length()-3);
            long posLong = Long.parseLong(posString);
            String posTime = TimeTool.secToString(posLong);

            if (durTime.substring(0, 2).equals("00")) {
                durTime = durTime.substring(3);
                posTime = posTime.substring(3);
            }

            // Do emoji
            float progFloat = (float) posLong/durLong;
            long progLong = (long) (progFloat / 0.1);

            String status;
            if (AudioTool.getServerAP(gmre.getGuild().getId()).isPaused()) {
                status = ":pause_button:";
            } else {
                status = ":arrow_forward:";
            }

            String progString = "";
            for (int i = 0; i < progLong; i++) {
                progString += "<:pf:667220899702898698>";
            }
            progString += "<:pm:667220547796729866>";
            for (int i = 0; i < (10-progLong-1); i++) {
                progString += "<:pe:667220547800924162>";
            }

            // Form string
            return  title + "\n" +
                    status + " | " + posTime + "/" + durTime + " | " + progString;

        } else {
            return null;
        }
    }

    public static String getQueueString(GuildMessageReceivedEvent gmre) {
        String out = "";
        TrackScheduler ts;

        if ((ts = getServerTS(gmre.getGuild().getId())) != null) {
            BlockingQueue<AudioTrack> q = ts.getQueue();

            if (q.size() == 0) {
                return "Empty";
            } else {
                int count = 1;
                for (AudioTrack at : q) {
                    out += count + " - " + at.getInfo().title + "\n";
                    count++;
                }
                return out;
            }
        }

        return null;
    }

    public static String getCurrentlyPlayingTitle(GuildMessageReceivedEvent gmre) {
        return getServerAP(gmre.getGuild().getId()).getPlayingTrack().getInfo().title;
    }

    public static int inSameChannel(GuildMessageReceivedEvent gmre) {
        VoiceChannel userChannel = gmre.getMember().getVoiceState().getChannel();
        String botChannel;

        if ((userChannel) != null) {
            if ((botChannel = gmre.getGuild().getAudioManager().getConnectedChannel().getId()) != null) {
                if (userChannel.getId().equals(botChannel)) {
                    return 1;   // same channel
                }
                return -1; // diff channels
            }
            return -2;  // bot not connected
        }
        return -3;  // user not in channel
    }

    // Connection property getters
    public static AudioPlayerManager getServerAPM(String serverID) {
        for (ConnectionData server : connections) {
            if (server.getServerID().equals(serverID)) {
                return server.getAPM();
            }
        }
        return null;
    }

    public static AudioPlayer getServerAP(String serverID) {
        for (ConnectionData server : connections) {
            if (server.getServerID().equals(serverID)) {
                return server.getAP();
            }
        }
        return null;
    }

    public static AudioLoadResultHandler getServerALRH(String serverID) {
        for (ConnectionData server : connections) {
            if (server.getServerID().equals(serverID)) {
                return server.getALRH();
            }
        }
        return null;
    }

    public static TrackScheduler getServerTS(String serverID) {
        for (ConnectionData server : connections) {
            if (server.getServerID().equals(serverID)) {
                return server.getTS();
            }
        }
        return null;
    }


    public static boolean exists(String serverID) {
        for (ConnectionData server : connections) {
            if (server.getServerID().equals(serverID)) {
                return true;
            }
        }
        return false;
    }
}

class ConnectionData {
    String serverID;
    AudioPlayerManager apm;
    AudioPlayer ap;
    AudioLoadResultHandler alrh;
    TrackScheduler ts;

    public ConnectionData(String serverID, AudioPlayerManager apm, AudioPlayer ap, AudioLoadResultHandler alrh, TrackScheduler ts) {
        this.serverID = serverID;
        this.apm = apm;
        this.ap = ap;
        this.alrh = alrh;
        this.ts = ts;
    }

    public String getServerID() {
        return serverID;
    }

    public AudioPlayerManager getAPM() {
        return apm;
    }

    public AudioPlayer getAP() {
        return ap;
    }

    public AudioLoadResultHandler getALRH() {
        return alrh;
    }

    public TrackScheduler getTS() {
        return ts;
    }
}
