package events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import static tools.VerifyMsgTool.*;

import java.util.regex.Pattern;

/*
 * Message listener for Generic events
 */
public class GenericEvent extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent gmre) {

        // Get message as raw String
        String msgIn = gmre.getMessage().getContentRaw();
        String cmdString = "";
        String msgOut = "";
        boolean msgSet = false;

        // Prints message to terminal
        System.out.println("== New Message ==");
        System.out.println("Server: " + gmre.getGuild().getName() + " (" + gmre.getGuild().getId() + ")");
        System.out.println("Channel: " + gmre.getChannel().getName() + " (" + gmre.getChannel().getId() + ")");
        System.out.println("Time: " + gmre.getMessage().getTimeCreated());
        System.out.println("User: " + gmre.getAuthor().getName() + " (" + gmre.getAuthor().getId() + ")");
        System.out.println("-- Message Below -- " + "\n" + msgIn + "\n");

        if (msgIn.length() > 1 && hasCorrectPrefix(gmre)) {
            cmdString = msgIn.toLowerCase().substring(1);
        }

        // Replies Pong!
        if (Pattern.matches("^(?i)ping$", cmdString) &&
                        (isBotCreator(gmre) ||
                        isBotAdmin(gmre) ||
                        isCmdChannel(gmre))) {
            msgOut = "Pong!";
            msgSet = true;

        // Replies World!
        } else if (Pattern.matches("^(?i)hello$", cmdString) &&
                        (isBotCreator(gmre) ||
                        isBotAdmin(gmre) ||
                        isCmdChannel(gmre))) {
            msgOut = "World!";
            msgSet = true;

        // Pings Apple, kek.
        } else if (Pattern.matches("^(?i)pa$", cmdString) &&
                (isBotCreator(gmre) ||
                        isBotAdmin(gmre))) {
            if (isBotAdmin(gmre)) {
                msgOut = "<@177473493816836098>";
                msgSet = true;
            }
        }

        // Displays message
        if (msgSet) {
            gmre.getChannel().sendMessage(msgOut).queue();
        }
    }
}
