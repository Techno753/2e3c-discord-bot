package events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.ImageTool;
import tools.RegexTool;
import tools.TagTool;
import tools.VerifyMsgTool;

import static tools.VerifyMsgTool.*;

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

        if (msgIn.length() > 1 && hasCorrectPrefix(gmre)) {
            cmdString = msgIn.toLowerCase().substring(1);
        }

        // Adds a text tag
        if (Pattern.matches("^(?is)att (\\w+)[\\s\\n](.+)$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {
            {
                // Attempt to add tag
                int result = TagTool.addTextTag(gmre, cmdString);

                if (result == 1) {
                    msgOut = "Created new text tag";
                } else if (result == -1) {
                    msgOut = "Error creating tag.";
                } else if (result == -2) {
                    msgOut = "Tag already exists. If you are the creator of this tag, use \"<prefix>utt <tag> <reply>\"";
                }
                msgSet = true;
            }

        // Adds an image tag
        } else if (Pattern.matches("^(?is)ait (\\w+)$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {
            {
                // Try to add image tag
                int resultA = TagTool.addImageTag(gmre, cmdString);
                if (resultA == 1) {
                    msgOut = "Tag created";
                } else if (resultA == -3) {
                    msgOut = "Tag already exists";
                } else if (resultA == -2) {
                    msgOut = "Failed to save tag";
                } else if (resultA == -1) {
                    msgOut = "Message must contain single image";
                }
                msgSet = true;
            }

            // Updates a tag
        } else if (Pattern.matches("asd", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {
            {
                System.out.println("etc");
            }
        // Removes a tag
        } else if (Pattern.matches("^(?i)rt (\\w+)$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {
            {
                String tag = RegexTool.getGroups("^(?i)rt (\\w+)$", cmdString).get(0);
                System.out.println("TEST 1");

                // check that user that wrote message owns tag
                if (VerifyMsgTool.isTagOwner(gmre, tag) || VerifyMsgTool.hasPrivs(gmre)) {
                    System.out.println("TEST 2");
                    int result = TagTool.removeTag(tag);

                    if (result == 1) {
                        msgOut = "Tag removed";
                    } else if (result == -1) {
                        msgOut = "Tag doesn't exist";
                    }
                } else {
                    msgOut = "You do not own this tag";
                }
                msgSet = true;
            }

        // Invokes a tag
        } else if (Pattern.matches("^(?i)t (\\w+)$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {
            {
                // Get tag from msg
                String tag = RegexTool.getGroups("^(?i)t (\\w+)$", cmdString).get(0);

                // Check that tag exists
                if (TagTool.tagExists(tag)) {
                    // Get tag data
                    String reply = TagTool.getReplyByTag(tag);
                    String type = TagTool.getTypeByTag(tag);

                    // Check if tag is text or image.
                    if (type.equals("text")) {
                        // do text tag stuff
                        msgOut = TagTool.getReplyByTag(tag);
                        msgSet = true;
                    } else if (type.equals("image")) {
                        // do image tag stuff
                        int result = ImageTool.uploadImageAsReply(gmre, reply, type);
                        if (result == -1) {
                            msgOut = "Image wasn't found";
                            msgSet = true;
                        }
                    }
                } else {
                    msgOut = "Tag doesn't exist";
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
