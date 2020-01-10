package events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.VerifyMsgTool;

import java.util.Random;
import java.util.regex.Pattern;

/*
 * Message Listener for RNG commands
 */
public class RNGEvent extends ListenerAdapter {

    Random rdm = new Random();

    // Message Listener for RNG Events
    public void onGuildMessageReceived(GuildMessageReceivedEvent gmre) {

        // Get message as raw String
        String msgIn = gmre.getMessage().getContentRaw();
        String cmdString = "";
        String msgOut = "";
        boolean msgSet = false;

        if (msgIn.length() > 1) {
            cmdString = msgIn.toLowerCase().substring(1);
        }

        if (Pattern.matches("^(?i)coin$", cmdString)) {
            if (VerifyMsgTool.hasCorrectPrefix(gmre)) {
                if (rdm.nextInt(2) > 0) {
                    msgOut = "Heads";
                } else {
                    msgOut = "Tails";
                }
                msgSet = true;
            }

        } else if (Pattern. matches("^(?i)dice$", cmdString)) {
            if (VerifyMsgTool.hasCorrectPrefix(gmre)) {
                msgOut = Integer.toString(rdm.nextInt(7));
                msgSet = true;
            }
        }

        if (msgSet) {
            gmre.getChannel().sendMessage(msgOut).queue();
        }
    }

}
