package events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.VerifyMsgTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

        if (msgIn.length() > 1 && VerifyMsgTool.hasCorrectPrefix(gmre)) {
            cmdString = msgIn.toLowerCase().substring(1);
        }

        // Replies heads or tails
        if (Pattern.matches("^(?i)coin$", cmdString)) {
            if (rdm.nextInt(2) > 0) {
                msgOut = "Heads";
            } else {
                msgOut = "Tails";
            }
            msgSet = true;

        // Replies a number between 1 and 6 inclusive
        } else if (Pattern.matches("^(?i)dice$", cmdString)) {
            msgOut = Integer.toString(rdm.nextInt(6) + 1);
            msgSet = true;

        // Rolls an n sided dice
        } else if (Pattern.matches("^(?i)d\\d+$", cmdString)) {
            int sides = Integer.parseInt(cmdString.substring(1));
            msgOut = Integer.toString(rdm.nextInt(sides) + 1);
            if (msgOut.length() > 1999) {
                msgOut = "Number too large. Please reduce the number of sides.";
            }
            msgSet = true;

        // Rolls an n sided dice multiple times
        } else if (Pattern.matches("^(?i)d\\d+ \\d+$", cmdString)) {
            int sides = Integer.parseInt(cmdString.split(" ")[0].substring(1));
            int times = Integer.parseInt(cmdString.split(" ")[1]);

            ArrayList<Integer> rolls = new ArrayList<>();
            for (int i = 0; i < times; i++) {
                rolls.add(rdm.nextInt(sides) + 1);
            }

            double sum = 0;
            for (int i : rolls) {
                sum += i;
            }
            double mean = sum / times;

            msgOut = "Rolls: " + rolls + "\n" +
                    "Average: " + mean + "\n" +
                    "Min: " + Collections.min(rolls) + "\n" +
                    "Max: " + Collections.max(rolls);
            if (msgOut.length() > 1999) {
                msgOut = "Numbers too large. Please reduce the number of sides or number of dice.";
            }
            msgSet = true;
        }

        if (msgSet) {
            gmre.getChannel().sendMessage(msgOut).queue();
        }
    }

}
