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

    public static void connectToVC(GuildMessageReceivedEvent gmre) {
        // Connect to channel
        AudioManager am = gmre.getGuild().getAudioManager();
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
    }

    public static void play(String s, GuildMessageReceivedEvent gmre) {
        // Get serverID
        String serverID = gmre.getGuild().getId();

        // Play song
        getServerAPM(serverID).loadItem(s, AudioTool.getServerALRH(serverID));
    }

    public static void updateChannel(GuildMessageReceivedEvent gmre) {
        System.out.println("this function is being called");
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
                toRemove = cd;
            }
        }
        if (toRemove != null) {
            connections.remove(toRemove);
        }

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
