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
        if (Pattern.matches("^(?i)mplay (.+)$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {
            {
                // get link
                String ytlink = RegexTool.getGroups("^(?is)mplay (.+)$", cmdString).get(0);
                System.out.println("Playing: " + ytlink);

                // Connect to correct channel
                if (AudioTool.connect(gmre) == -1) {
                    msgOut = "User is not in a voice channel";
                } else {
                    // Queue song
                    AudioTool.queue(ytlink, gmre);
                    msgOut = "Queued: " + YTTool.getTitleByID(ytlink);
                }
                msgSet = true;
            }

        // Disconnects bot from channel
        } else if (Pattern.matches("^(?i)mdc$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {
            {
                // Disconnects from the channel
                AudioTool.disconnectFromVC(gmre);
            }

        } else if (Pattern.matches("^(?i)msearch (.+)$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {
            {
                // Get search term
                ArrayList<String> terms = RegexTool.getGroups("^(?is)msearch (.+)$", cmdString);
                String query = terms.get(0);

                // Display search results
                searchResult = YTTool.search(query);

                // Set waitingForResponse
                waitingForResponse = true;
                lastUser = gmre.getAuthor().getId();

                msgOut = "Search results for: " + query + "\n";
                for (int i = 0; i < searchResult.size(); i++) {
                    msgOut += (i + 1) + " - " + searchResult.get(i).get(0) + "\n";
                }
                msgOut += "Waiting for reply from: " + gmre.getGuild().getMemberById(lastUser).getUser().getName();
                msgSet = true;
            }

        // Queues video chosen by user
        } else if (Pattern.matches("^(?i)mpick [1-9]|10$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {
            {
                // If waiting for response from search
                if (waitingForResponse && gmre.getAuthor().getId().equals(lastUser)) {

                    // Connect to correct channel
                    if (AudioTool.connect(gmre) == -1) {
                        msgOut = "User is not in a voice channel";
                    } else {

                        // Get video to play
                        String number = RegexTool.getGroups("^(?i)mpick ([1-9]|10)$", cmdString).get(0);
                        String videoID = searchResult.get(Integer.parseInt(number)).get(1);

                        msgOut = "Queued: " + searchResult.get(Integer.parseInt(number) - 1).get(0);
                        AudioTool.queue(videoID, gmre);

                        waitingForResponse = false;
                    }
                    msgSet = true;
                }
            }
        }

        // Displays message
        if (msgSet) {
            gmre.getChannel().sendMessage(msgOut).queue();
        }
    }
}
