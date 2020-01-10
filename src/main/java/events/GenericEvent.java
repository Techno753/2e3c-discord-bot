package events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/*
 * Message listener for Generic events
 */
public class GenericEvent extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent gmre) {

        // Get message as raw String
        String msgIn = gmre.getMessage().getContentRaw();
        String msgOut = "";

        switch (msgIn.toLowerCase()) {
            case "ping":
                msgOut = "Pong";
                break;

            case "hello":
                msgOut = "Hi!";
                break;

            case "pa":
                msgOut = "<@177473493816836098>";
                break;
        }

        System.out.println(msgOut);

        if (msgOut.length() > 0) {
            gmre.getChannel().sendMessage(msgOut).queue();
        }
    }

}
