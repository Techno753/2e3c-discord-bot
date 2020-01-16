package tools;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import tools.audioLoader.MyAudioLoadResultHandler;
import tools.audioLoader.TrackScheduler;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public final class AudioTool {
    private static ArrayList<ConnectionData> connections = new ArrayList<>();

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
        player.addListener(ts);
        MyAudioLoadResultHandler audioLoader = new MyAudioLoadResultHandler(ts);

        connections.add(new ConnectionData(gmre.getGuild().getId(), apm, audioLoader, ts));
        return 1;
    }

    public static int connect(GuildMessageReceivedEvent gmre) {
        // Get serverID
        String serverID = gmre.getGuild().getId();

        // if user not in vc then reply message
        if (gmre.getMember().getVoiceState().getChannel() == null) {
            //msgOut = "User is not in a voice channel";
            System.out.println("this");
            return -1;

            // if bot not in any channel then connect to user's channel
        } else if (!AudioTool.exists(serverID)) {
            System.out.println("that");
            AudioTool.connectToVC(gmre);
            // if bot in incorrect channel then move
        } else if (!gmre.getGuild().getAudioManager().getConnectedChannel().getId()
                .equals(gmre.getMember().getVoiceState().getChannel().getId())) {
            gmre.getGuild().getAudioManager().getConnectedChannel().getId();
            AudioTool.updateChannel(gmre);
        }
        return 1;
    }

    public static void queue(String s, GuildMessageReceivedEvent gmre) {
        // Get serverID
        String serverID = gmre.getGuild().getId();

        // Play song
        getServerAPM(serverID).loadItem(s, AudioTool.getServerALRH(serverID));
    }

    public static void updateChannel(GuildMessageReceivedEvent gmre) {
        System.out.println("Moving channels...");
        AudioManager am = gmre.getGuild().getAudioManager();
        VoiceChannel vc = gmre.getMember().getVoiceState().getChannel();
        am.closeAudioConnection();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        am.openAudioConnection(vc);

        System.out.println(connections);
    }

    public static void disconnectFromVC(GuildMessageReceivedEvent gmre) {
        ConnectionData toRemove = null;

        for (ConnectionData cd : connections) {
            if (cd.getServerID().equals(gmre.getGuild().getId())) {
                // clear tracks
                cd.getTS().clear();
                // set ConnectionData to remove
                toRemove = cd;
            }
        }
        if (toRemove != null) {
            connections.remove(toRemove);
        }

        // Close channel connection
        gmre.getGuild().getAudioManager().closeAudioConnection();
    }

    public static AudioPlayerManager getServerAPM(String serverID) {
        for (ConnectionData server : connections) {
            if (server.getServerID().equals(serverID)) {
                return server.getAPM();
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
    AudioLoadResultHandler alrh;
    TrackScheduler ts;

    public ConnectionData(String serverID, AudioPlayerManager apm, AudioLoadResultHandler alrh, TrackScheduler ts) {
        this.serverID = serverID;
        this.apm = apm;
        this.alrh = alrh;
        this.ts = ts;
    }

    public String getServerID() {
        return serverID;
    }

    public AudioPlayerManager getAPM() {
        return apm;
    }

    public AudioLoadResultHandler getALRH() {
        return alrh;
    }

    public TrackScheduler getTS() {
        return ts;
    }
}
