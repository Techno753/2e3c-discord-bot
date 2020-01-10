package events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Random;
/*
 * Message Listener for RNG commands
 */
public class RNGEvent extends ListenerAdapter {

    Random rdm = new Random();

    // Message Listener for RNG Events
    public void onGuildMessageReceived(GuildMessageReceivedEvent gmre) {

        // Get message as raw String
        String msgIn = gmre.getMessage().getContentRaw();
        String msgOut = "";

        switch (msgIn.toLowerCase()) {
            case "coin":
                if (rdm.nextInt(2) > 0) {
                    msgOut = "Heads";
                } else {
                    msgOut = "Tails";
                }
                break;

            case "dice":
                msgOut = Integer.toString(rdm.nextInt(7));
        }

        if (msgOut.length() > 0) {
            gmre.getChannel().sendMessage(msgOut).queue();
        }
    }

}
