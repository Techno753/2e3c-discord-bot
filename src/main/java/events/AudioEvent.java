package events;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import tools.*;
import tools.audioLoader.MyAudioLoadResultHandler;
import tools.audioLoader.TrackScheduler;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static tools.VerifyMsgTool.*;

public class AudioEvent extends ListenerAdapter {
    // Variables for searching for video
    boolean waitingForResponse = false;
    String lastUser = "";
    ArrayList<ArrayList<String>> searchResult = new ArrayList<>();

    public void onGuildMessageReceived(GuildMessageReceivedEvent gmre) {

        // Get message as raw String
        String msgIn = gmre.getMessage().getContentRaw();
        String cmdString = "";
        String msgOut = "";
        boolean msgSet = false;

        if (msgIn.length() > 1 && hasCorrectPrefix(gmre)) {
            cmdString = msgIn.substring(1);
        }

        // Plays video link
        if (Pattern.matches("^(?i)testaudio (.+)$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {

            String ytlink = RegexTool.getGroups("^(?is)testaudio (.+)$", cmdString).get(0);
            {
                // Get serverID
                String serverID = gmre.getGuild().getId();

                // if not in server connect
                if (!AudioTool.exists(serverID)) {
                    AudioTool.connectToVC(gmre);

                // if in server not correct channel then update channel
                } else if (!gmre.getGuild().getAudioManager().getConnectedChannel().getId()
                        .equals(gmre.getMember().getVoiceState().getChannel().getId())) {
                    gmre.getGuild().getAudioManager().getConnectedChannel().getId();
                    AudioTool.updateChannel(gmre);
                }
                // if in correct channel do nothing

                // Play song
                AudioTool.getServerAPM(serverID).loadItem(ytlink, AudioTool.getServerALRH(serverID));
            }

        // Disconnects bot from channel
        } else if (Pattern.matches("^(?i)disconnect$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {

            // Disconnects from the channel
            AudioTool.disconnectFromVC(gmre);
        } else if (Pattern.matches("^(?i)testsearch (.+)$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {

                System.out.println("testing search");

                // Get search term
                ArrayList<String> terms = RegexTool.getGroups("^(?is)testsearch (.+)$", cmdString);
                String query = terms.get(0);

                // Display search results
                searchResult = YTTool.search(query);

                // Set waitingForResponse
                waitingForResponse = true;
                lastUser = gmre.getAuthor().getId();

        } else if (Pattern.matches("^(?i)pick [1-9]|10$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {

            // If waiting for response from search
            if (waitingForResponse && gmre.getAuthor().getId().equals(lastUser)) {

                // Connect to VC and set up player if not in (correct) channel
                // Get serverID
                String serverID = gmre.getGuild().getId();

                // if not in server connect
                if (!AudioTool.exists(serverID)) {
                    AudioTool.connectToVC(gmre);

                // if in server not correct channel then update channel
                } else if (!gmre.getGuild().getAudioManager().getConnectedChannel().getId()
                        .equals(gmre.getMember().getVoiceState().getChannel().getId())) {
                    gmre.getGuild().getAudioManager().getConnectedChannel().getId();
                    AudioTool.updateChannel(gmre);
                }
                // if in correct channel do nothing

                // Play song
                // AudioTool.getServerAPM(serverID).loadItem(ytlink, AudioTool.getServerALRH(serverID));

                // Get number
                ArrayList<String> terms = RegexTool.getGroups("^(?i)pick ([1-9]|10)$", cmdString);
                String number = terms.get(0);
                String videoID = searchResult.get(Integer.parseInt(number)).get(1);

                AudioTool.play(videoID, gmre);

                waitingForResponse = false;
            }
        }
    }
}
