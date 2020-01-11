package events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.VerifyMsgTool;

import java.util.regex.Pattern;

/**
 * Used to test things.
 */
public class TestEvent extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent gmre) {
        // Get message as raw String
        String msgIn = gmre.getMessage().getContentRaw();
        String cmdString = "";
        String msgOut = "";
        boolean msgSet = false;

        if (msgIn.length() > 1 && VerifyMsgTool.hasCorrectPrefix(gmre)) {
            cmdString = msgIn.toLowerCase().substring(1);
        }

        // Tests is user is a bot admin
        if (Pattern.matches("^(?i)batest$", cmdString)) {
            if (VerifyMsgTool.isBotAdmin(gmre)) {
                msgOut = "User is a bot admin.";
            } else {
                msgOut = "User is not a bot admin.";
            }
            msgSet = true;
        }

        if (msgSet) {
            gmre.getChannel().sendMessage(msgOut).queue();
        }
    }
}
