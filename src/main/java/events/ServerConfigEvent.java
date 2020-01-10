package events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.ConfigTool;
import tools.VerifyMsgTool;

import java.util.ArrayList;
import java.util.regex.Pattern;

// Event Listener for Server related events
public class ServerConfigEvent extends ListenerAdapter {

    // Adds server to server configs
    @Override
    public void onGuildJoin(GuildJoinEvent gje) {
        try {
            System.out.println("Attempting to add server");
            System.out.println(gje.getGuild().getName());
            System.out.println(gje.getGuild().getId());
            ConfigTool.addServer(gje.getGuild().getName(), gje.getGuild().getId(), gje.getGuild().getOwnerId());
            System.out.println("Successfully joined new server");
        } catch (Exception e) {
            System.out.println("Error adding server");
        }
    }

    // Removes server from server configs
    @Override
    public void onGuildLeave(GuildLeaveEvent gle) {
        try {
            System.out.println("Attempting to Leave server");
            System.out.println(gle.getGuild().getId());
            ConfigTool.removeServer(gle.getGuild().getId());
            System.out.println("Successfully removed server");
        } catch (Exception e) {
            System.out.println("Error removing server");
        }
    }

    // Displays server and channel info
    public void onGuildMessageReceived(GuildMessageReceivedEvent gmre) {
        // Get message as raw String
        String msgIn = gmre.getMessage().getContentRaw();
        String msgOut = "";
        boolean msgSet = false;

        String cmdString = msgIn.toLowerCase().substring(1);

        // Gets information on all servers
        if (Pattern.matches("^servers$", cmdString)) {
            if (VerifyMsgTool.isBotCreator(gmre) && VerifyMsgTool.hasCorrectPrefix(gmre)) {
                msgOut = ConfigTool.getStringAll(gmre);
                msgSet = true;
            }

        // Gets information on the server messaged on
        } else if (Pattern.matches("^thisserver$", cmdString)) {
            if ((VerifyMsgTool.isBotAdmin(gmre) || VerifyMsgTool.isBotCreator(gmre))
                    || VerifyMsgTool.hasCorrectPrefix(gmre)) {

                msgOut = ConfigTool.getStringByID(gmre.getGuild().getId(), gmre);
                msgSet = true;
            }

        // Changes the command prefix for a server
        } else if (Pattern.matches("^prefix .$", cmdString)) {
            if ((VerifyMsgTool.isBotAdmin(gmre) && VerifyMsgTool.hasCorrectPrefix(gmre))
                    || VerifyMsgTool.isBotCreator(gmre)) {
                String prefix = msgIn.split(" ")[1];
                ConfigTool.setServerPrefixByID(gmre.getGuild().getId(),
                        msgIn.split(" ")[1]);
                ConfigTool.writeJson();
                msgOut = "Server prefix set to: " + prefix;
                msgSet = true;
            }
        }

        // Queue message to send if there is one to send
        if (msgSet) {
            gmre.getChannel().sendMessage(msgOut).queue();
        }

        // Clear message flag
//        msgSet = false;
    }
}
