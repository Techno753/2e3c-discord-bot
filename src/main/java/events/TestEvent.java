package events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.ImageTool;
import tools.RegexTool;
import tools.VerifyMsgTool;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static tools.VerifyMsgTool.hasPrivs;
import static tools.VerifyMsgTool.isCmdChannel;

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
        if (Pattern.matches("^(?i)yttest (.+)$", cmdString) &&
                (isCmdChannel(gmre) || hasPrivs(gmre))) {

            ArrayList<String> terms = RegexTool.getGroups("^(?is)testaudio (.+)$", cmdString);
            String term = terms.get(0);

            System.out.println("Searching: " + term);
        }


        if (msgSet) {
            gmre.getChannel().sendMessage(msgOut).queue();
        }
    }
}
