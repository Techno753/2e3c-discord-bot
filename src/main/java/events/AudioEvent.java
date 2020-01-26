package events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.channel.voice.GenericVoiceChannelEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.*;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static tools.VerifyMsgTool.*;

public class AudioEvent extends ListenerAdapter {
    EmbedBuilder eb = new EmbedBuilder();

    // Variables for searching for video
    boolean waitingForResponse = false;
    String lastUser = "";
    ArrayList<ArrayList<String>> searchResult = new ArrayList<>();

    public void onGuildMessageReceived(GuildMessageReceivedEvent gmre) {

        // Get message as raw String
        String msgIn = gmre.getMessage().getContentRaw();
        String cmdString = "";
        String msgOut = "";
        String msgType = "";
        boolean msgSet = false;

        if (msgIn.length() > 1 && hasCorrectPrefix(gmre)) {
            cmdString = msgIn.substring(1);
        }

        // Queues video link
        if (Pattern.matches("^(?i)mplay (.+)$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {
            {
                // get link
                String ytlink = RegexTool.getGroups("^(?is)mplay (.+)$", cmdString).get(0);

                // Connect to correct channel
                if (AudioTool.connect(gmre) == -1) {
                    msgOut = "User is not in a voice channel";
                } else {
                    // Queue song TODO check if can get title of queued track
                    String[] info;
                    if ((info = AudioTool.queue(ytlink, gmre)) != null) {
                        msgOut = "Queued " + info[1] + ": " + info[0];
                    } else {
                        msgOut = "Unable to find video.";
                    }
                }
                msgSet = true;
            }

        // Searches YouTube
        } else if (Pattern.matches("^(?i)msearch (.+)$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {
            {
                // Get search term
                String query = RegexTool.getGroups("^(?is)msearch (.+)$", cmdString).get(0);

                // Display search results
                searchResult = YTTool.search(query);

                // Set waitingForResponse
                waitingForResponse = true;
                lastUser = gmre.getAuthor().getId();

                // Set embed
                eb.setTitle("YouTube Search Results for \"" + query + "\"");
                String embedBody = "";
                for (int i = 0; i < searchResult.size(); i++) {
                    embedBody += (i + 1) + " - " + searchResult.get(i).get(1) + " (" + searchResult.get(i).get(2) + ")" + "\n";
                }
                eb.addField("Results:", StringTool.escapeHTML(embedBody), false);
                eb.addField("", "Select a song using `" + ConfigTool.getBotPrefixByID(gmre.getGuild().getId()) + "mpick <1 - 5>`\n" +
                        "Waiting for reply from: " + gmre.getGuild().getMemberById(lastUser).getUser().getName(), false);
                msgType = "embed";
                msgSet = true;
            }

        // Queues video chosen by user TODO Separate out channel moving
        } else if (Pattern.matches("^(?i)mpick [1-5]$", cmdString) &&
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
                        String videoID = searchResult.get(Integer.parseInt(number) - 1).get(0);

                        msgOut = "Queued: " + searchResult.get(Integer.parseInt(number) - 1).get(1);
                        AudioTool.queue(videoID, gmre);

                        // unset waiting for response
                        waitingForResponse = false;
                        lastUser = "";
                    }
                    msgSet = true;
                }
            }

        // Gets current playing
        } else if (Pattern.matches("^(?i)mnp$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {
            {
                if (AudioTool.exists(gmre.getGuild().getId())) {
                    // Disconnects from the channel
                    String status;
                    if ((status = AudioTool.getStatusString(gmre)) != null) {
                        eb.setTitle("Currently Playing");
                        eb.addField(StringTool.escapeHTML(status.split("\n")[0]), status.split("\n")[1], false);
                        msgType = "embed";
                    } else {
                        msgOut = "Not currently playing";
                    }
                } else {
                    msgOut = "Bot not connected";
                }
                msgSet = true;
            }

        // Gets queue
        } else if (Pattern.matches("^(?i)mq$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {
            {
                // gets queue
                String queue = AudioTool.getQueueString(gmre);

                eb.setTitle("Queue");
                if (queue == null) {
                    msgOut = "Bot not in voice channel";
                } else {
                    eb.addField("", StringTool.escapeHTML(queue), false);
                    msgType = "embed";
                }
                msgSet = true;
            }

        // Skips current song TODO
        } else if (Pattern.matches("^(?i)mskip$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {
            {
                if (AudioTool.inSameChannel(gmre) == 1) {
                    // Skips current song
                    int result = AudioTool.skip(gmre);
                    if (result == 1) {
                        msgOut = "Skipping current song";
                    } else if (result == -2) {
                        msgOut = "Not playing any music";
                    } else if (result == -1) {
                        msgOut = "Bot not connected";
                    }
                } else {
                    msgOut = "User not in the same channel as bot";
                }
                msgSet = true;
            }

        // Skips to given song index TODO
        } else if (Pattern.matches("^(?i)mskipto ([\\d]+)$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {
            {
                if (AudioTool.inSameChannel(gmre) == 1) {
                    // get argument
                    int ind = Integer.parseInt(RegexTool.getGroups("^(?i)mskipto ([\\d]+)$", cmdString).get(0));

                    // check num skips is less than songs remaining
                    if (ind <= AudioTool.getServerTS(gmre.getGuild().getId()).getQueue().size()) {
                        // Skips to song
                        int result = AudioTool.skipN(gmre, ind);
                        if (result == 1) {
                            msgOut = "Skipping to: " + AudioTool.getCurrentlyPlayingTitle(gmre);
                        } else if (result == -2) {
                            msgOut = "Not playing any music";
                        } else if (result == -1) {
                            msgOut = "Bot not connected";
                        }
                    } else {
                        msgOut = "There aren't that many songs in queue";
                    }
                } else {
                    msgOut = "User not in the same channel as bot";
                }
                msgSet = true;
            }

        // Pauses currently playing song
        } else if (Pattern.matches("^(?i)mpause$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {
            {
                if (AudioTool.inSameChannel(gmre) == 1) {
                    int result = AudioTool.pausePlayback(gmre);
                    if (result == 1) {
                        msgOut = "Paused";
                    } else if (result == -3) {
                        msgOut = "Bot already paused";
                    } else if (result == -2) {
                        msgOut = "Bot not playing";
                    } else if (result == -1) {
                        msgOut = "Bot not connected";
                    }
                } else {
                    msgOut = "User not in the same channel as bot";
                }
                msgSet = true;
            }

        // Resumes currently playing song
        } else if (Pattern.matches("^(?i)mresume$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {
            {
                if (AudioTool.inSameChannel(gmre) == 1) {
                    int result = AudioTool.resumePlayback(gmre);
                    if (result == 1) {
                        msgOut = "Resumed";
                    } else if (result == -3) {
                        msgOut = "Bot already playing";
                    } else if (result == -2) {
                        msgOut = "Bot not playing";
                    } else if (result == -1) {
                        msgOut = "Bot not connected";
                    }
                } else {
                    msgOut = "User not in the same channel as bot";
                }

                msgSet = true;
            }

        // Clears the ends current song and clears the queue
        } else if (Pattern.matches("^(?i)mclear$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {
            {
                if (AudioTool.inSameChannel(gmre) == 1) {

                    // Skips current song
                    int result = AudioTool.clear(gmre);
                    if (result == 1) {
                        msgOut = "Skipped current music and cleared queue";
                    } else if (result == -1) {
                        msgOut = "Bot not connected";
                    }
                } else {
                    msgOut = "User is not connected to the same channel as bot";
                }
                msgSet = true;
            }

        // Moves bot to user's channel
        } else if (Pattern.matches("^(?i)mmove$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {
            {
                // Moves bot to user's channel
                if (AudioTool.connect(gmre) == -1) {
                    msgOut = "User is not in a voice channel";
                } else {
                    // Queue song TODO check if can get title of queued track
                    msgOut = "Moved channel";
                }
                msgSet = true;
            }

        // Disconnects bot from channel
        } else if (Pattern.matches("^(?i)mdc$", cmdString) &&
            (isCmdChannel(gmre) || hasPrivs(gmre))) {
            {
                // Disconnects from the channel
                AudioTool.disconnectFromVC(gmre.getGuild());
            }
        }
        // Displays message
        if (msgSet) {
            if (msgType.equals("embed")) {
                gmre.getChannel().sendMessage(eb.build()).queue();
                eb.clear();
            } else {
                gmre.getChannel().sendMessage(StringTool.escapeHTML(msgOut)).queue();
            }
        }
    }

    //public void (Voi)
}
