package events;

import net.dv8tion.jda.api.entities.PrivateChannel;
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
        String  msgOut = "";
        String msgType = "";
        boolean msgSet = false;

        // Prints message to terminal
//        System.out.println("== New Message ==");
//        System.out.println("Server: " + gmre.getGuild().getName() + " (" + gmre.getGuild().getId() + ")");
//        System.out.println("Channel: " + gmre.getChannel().getName() + " (" + gmre.getChannel().getId() + ")");
//        System.out.println("Time: " + gmre.getMessage().getTimeCreated());
//        System.out.println("User: " + gmre.getAuthor().getName() + " (" + gmre.getAuthor().getId() + ")");
//        System.out.println("-- Message Below -- " + "\n" + msgIn + "\n");

        if (msgIn.length() > 1 && hasCorrectPrefix(gmre)) {
            cmdString = msgIn.toLowerCase().substring(1);
        }

        // Replies Pong!
        if (Pattern.matches("^(?i)ping$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {
            msgOut = "Pong!";
            msgSet = true;

        // Replies World!
        } else if (Pattern.matches("^(?i)hello$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {
            msgOut = "World!";
            msgSet = true;

        } else if (Pattern.matches("^(?i)\\^help$", msgIn)) {
            msgOut = "All documentation on how to set up and use the bot at: https://github.com/Techno753/2e3c-discord-bot/blob/master/README.md";
            msgType = "msgTo";
            msgSet = true;

        // Pings Apple, kek.
        } else if (Pattern.matches("^(?i)pa$", cmdString) &&
            hasPrivs(gmre)) {
            msgOut = "<@177473493816836098>";
            msgSet = true;
        }

        // Displays message
        if (msgSet) {
            if (msgType == "msgTo") {
                final String msgOutFinal = msgOut;
                gmre.getAuthor().openPrivateChannel().queue(channel -> { // this is a lambda expression
                    // the channel is the successful response
                    channel.sendMessage(msgOutFinal).queue();
                });
            } else {
                gmre.getChannel().sendMessage(msgOut).queue();
            }
        }
        msgType = "";
    }
}
