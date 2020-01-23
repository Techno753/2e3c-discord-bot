package events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.ConfigTool;
import tools.TagTool;

import java.util.regex.Pattern;

import static tools.VerifyMsgTool.*;

/**
 * Code to execute upon the bot readying
 */
public class StartEvent extends ListenerAdapter {

    public void onReady(ReadyEvent re) {
        ConfigTool.readConfig();
        ConfigTool.updateServers(re.getJDA());
        TagTool.readTags();
        re.getJDA().getPresence().setActivity(Activity.playing("^help"));
        System.out.println("Ready!");
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent gmre) {
        // Get message as raw String
        String msgIn = gmre.getMessage().getContentRaw();
        String cmdString = "";
        String msgOut = "";
        boolean msgSet = false;
        String msgType = "";
        EmbedBuilder eb = new EmbedBuilder();

        if (msgIn.length() > 1 && hasCorrectPrefix(gmre)) {
            cmdString = msgIn.toLowerCase().substring(1);
        }

        // Shuts down the bot
        if (Pattern.matches("^(?is)shutdown$", cmdString) &&
                isBotCreator(gmre)) {
            {
                // shutdown bot
                System.out.println("Shutting down bot");
                gmre.getJDA().shutdown();
                System.exit(0);
            }
        }
    }
}