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
import java.util.regex.Matcher;
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
        String cmdString = "";
        boolean msgSet = false;

        if (msgIn.length() > 1) {
            cmdString = msgIn.substring(1);
        }

        // Gets information on all servers
        if (Pattern.matches("^(?i)servers$", cmdString)) {
            if (VerifyMsgTool.isBotCreator(gmre) && VerifyMsgTool.hasCorrectPrefix(gmre)) {
                msgOut = ConfigTool.getStringAll(gmre.getJDA());
                msgSet = true;
            }

        // Gets information on the server messaged on
        } else if (Pattern.matches("^(?i)thisserver$", cmdString)) {
            if ((VerifyMsgTool.isBotAdmin(gmre) || VerifyMsgTool.isBotCreator(gmre))
                    || VerifyMsgTool.hasCorrectPrefix(gmre)) {

                msgOut = ConfigTool.getStringByID(gmre.getGuild().getId(), gmre.getJDA());
                msgSet = true;
            }

        // Changes the command prefix for a server
        } else if (Pattern.matches("^(?i)prefix .$", cmdString)) {
            if ((VerifyMsgTool.isBotAdmin(gmre) && VerifyMsgTool.hasCorrectPrefix(gmre))
                    || VerifyMsgTool.isBotCreator(gmre)) {
                String prefix = msgIn.split(" ")[1];
                ConfigTool.setServerPrefixByID(gmre.getGuild().getId(),
                        msgIn.split(" ")[1]);
                ConfigTool.writeConfig();
                msgOut = "Server prefix set to: " + prefix;
                msgSet = true;
            }

        // Adds a bot admin for the server
        } else if (Pattern.matches("^(?i)addba <@!?\\d+>$", cmdString)) {
            Pattern addbaPat = Pattern.compile("^(?i)addba <@!?(\\d+)>$");
            Matcher m = addbaPat.matcher(cmdString);

            // Get user ID from command
            if (m.find()) {
                String userID = m.group(1);

                // Check that user exists on the server
                int result = ConfigTool.addBotAdminByID(gmre.getGuild().getId(), userID, gmre.getJDA());
                if (result == 1) {
                    msgOut = "Successfully added bot admin: " + gmre.getGuild().getMemberById(userID).getUser().getName();
                    ConfigTool.writeConfig();
                } else if (result == -3) {
                    msgOut = "Error adding bot admin.";
                } else if (result == -2) {
                    msgOut = "Error adding bot admin. User not found on this server.";
                } else if (result == -1) {
                    msgOut = "Error adding bot admin. Server not found.";
                }
                msgSet = true;
            }

        // Removes a bot admin for the server
        } else if (Pattern.matches("^(?i)remba <@!?\\d+>$", cmdString)) {
            Pattern rembaPat = Pattern.compile("^(?i)remba <@!?(\\d+)>$");
            Matcher m = rembaPat.matcher(cmdString);

            // Get user ID from command
            if (m.find()) {
                String userID = m.group(1);

                int result = ConfigTool.removeBotAdminByID(gmre.getGuild().getId(), userID, gmre.getJDA());
                if (result == 1) {
                    msgOut = "Successfully removed bot admin: " + gmre.getGuild().getMemberById(userID).getUser().getName();
                    ConfigTool.writeConfig();
                } else if (result == -3) {
                    msgOut = "Error removing bot admin. User is not an existing bot admin.";
                } else if (result == -2) {
                    msgOut = "Error removing bot admin. User was not found on this server.";
                } else if (result == -1) {
                    msgOut = "Error removing bot admin. Server not found.";
                }
                msgSet = true;
            }


        // Removes a bot admin for the server
        } else if (Pattern.matches("^(?i)remba <@!?\\d+>$", cmdString)) {
            Pattern rembaPat = Pattern.compile("^(?i)remba <@!?(\\d+)>$");
            Matcher m = rembaPat.matcher(cmdString);

            if (m.find()) {
                String userID = m.group(1);
                System.out.println(userID);
            }
        }

        // Queue message to send if there is one to send
        if (msgSet) {
            gmre.getChannel().sendMessage(msgOut).queue();
        }
    }
}
