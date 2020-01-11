package events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.RegexTool;
import tools.TagTool;
import tools.VerifyMsgTool;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Allows users to associate text and images with text and then invoke them
 */
public class TagEvent extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent gmre) {
        // Get message as raw String
        String msgIn = gmre.getMessage().getContentRaw();
        String cmdString = "";
        String msgOut = "";
        boolean msgSet = false;

        if (msgIn.length() > 1 && VerifyMsgTool.hasCorrectPrefix(gmre)) {
            cmdString = msgIn.toLowerCase().substring(1);
        }

        // Adds a text tag
        if (Pattern.matches("^(?is)att (\\w+) (.+)$", cmdString)) {
            ArrayList<String> terms = RegexTool.getGroups("^(?is)att (\\w+) (.+)$", cmdString);
            String tag = terms.get(0);
            String reply = terms.get(1);
            String type = "text";
            String userID = gmre.getAuthor().getId();

            // Check if tag exists and ask user to update

            // add tag
            int result = TagTool.addTag(tag, reply, type, userID, gmre.getJDA());

            if (result == 1) {
                msgOut = "Created new text tag:\n" +
                        "Tag: " + tag + "\n" +
                        "Reply: " + reply;
            } else if (result == -1) {
                msgOut = "Error creating tag.";
            } else if (result == -2) {
                msgOut = "Tag already exists. If you are the creator of this tag, use \"<prefix>utt <tag> <reply>\"";
            }
            msgSet = true;

        // Adds an image tag

        // Removes a tag
        } else if (Pattern.matches("^(?i)rtt (\\w+)$", cmdString)) {
            String tag = RegexTool.getGroups("^(?i)rtt (\\w+)$", cmdString).get(0);

            int result = TagTool.removeTag(tag);

            if (result == 1) {
                msgOut = "Removed text tag:\n" +
                        "Tag: " + tag;
            } else if (result == -1) {
                msgOut = "Tag doesn't exist.";
            }
            msgSet = true;

        // Invokes a tag
        } else if (Pattern.matches("^(?i)t (\\w+)$", cmdString)) {
            String tag = RegexTool.getGroups("^(?i)t (\\w+)$", cmdString).get(0);

            msgOut = TagTool.getReplyByTag(tag);;
            msgSet = true;
        }

        // Displays message
        if (msgSet) {
            gmre.getChannel().sendMessage(msgOut).queue();
        }
    }
}
