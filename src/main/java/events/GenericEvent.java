package events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.ConfigTool;
import tools.VerifyMsgTool;

import java.util.regex.Pattern;

/*
 * Message listener for Generic events
 */
public class GenericEvent extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent gmre) {

        // Get message as raw String
        String msgIn = gmre.getMessage().getContentRaw();
        String msgOut = "";
        Boolean msgSet = false;

        String cmdString = msgIn.toLowerCase().substring(1);
        System.out.println("input: " + cmdString);

        // Replies Pong!
        if (Pattern.matches("^(?i)ping$", cmdString)) {
            if (VerifyMsgTool.hasCorrectPrefix(gmre)) {
                msgOut = "Pong!";
                msgSet = true;
            }

        // Replies World!
        } else if (Pattern.matches("^(?i)hello$", cmdString)) {
            msgOut = "World!";
            msgSet = true;

        // Pings Apple
        } else if (Pattern.matches("^(?i)pa$", cmdString)) {
            msgOut = "<@177473493816836098>";
            msgSet = true;
        }

        if (msgSet) {
            gmre.getChannel().sendMessage(msgOut).queue();
        }
    }
}
