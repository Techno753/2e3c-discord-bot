package events;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import tools.ConfigTool;
import tools.RegexTool;
import tools.VerifyMsgTool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Event listener for server config related events
 */
public class ServerConfigEvent extends ListenerAdapter {

    /**
     * Adds a new server config upon joining a server
     * @param gje Server join event
     */
    @Override
    public void onGuildJoin(GuildJoinEvent gje) {
        try {
            System.out.println("Attempting to add server...");
            System.out.println(gje.getGuild().getName() + " (" + gje.getGuild().getId() + ")");
            int result = ConfigTool.addServer(gje.getGuild().getId(), gje.getJDA());
            if (result == 1) {
                System.out.println("Successfully joined new server.");
            } else if (result == -2) {
                System.out.println("Error adding server. Server data already exists in config.");
            } else if (result == -1) {
                System.out.println("Error adding server inner.");
            }
        } catch (Exception e) {
            System.out.println("Error adding server outer.");
        }
    }

    /**
     * Removes server config upon leaving a server
     * @param gle Server leave event
     */
    @Override
    public void onGuildLeave(GuildLeaveEvent gle) {
        try {
            System.out.println("Attempting to Leave server...");
            System.out.println(gle.getGuild().getName() + " (" + gle.getGuild().getId() + ")");
            int result = ConfigTool.removeServer(gle.getGuild().getId());
            if (result == 1) {
                System.out.println("Successfully removed server.");
            } else if (result == -1) {
                System.out.println("Error removing server. Server info was not found in config.");
            }
        } catch (Exception e) {
            System.out.println("Error removing server.");
        }
    }

    /**
     * Displays server config information
     * @param gmre Message received event
     */
    public void onGuildMessageReceived(GuildMessageReceivedEvent gmre) {

        // Initialize variables to use within scope
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
        } else if (Pattern.matches("^(?i)server$", cmdString)) {
            if ((VerifyMsgTool.isBotAdmin(gmre) || VerifyMsgTool.isBotCreator(gmre))
                    || VerifyMsgTool.hasCorrectPrefix(gmre)) {

                msgOut = ConfigTool.getStringByID(gmre.getGuild().getId(), gmre.getJDA());
                msgSet = true;
            }

        // Updates server configs if they mismatch
        } else if (Pattern.matches("^(?i)updateservers$", cmdString)) {
            ConfigTool.updateServers(gmre.getJDA());

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
            String userID = RegexTool.getGroups("^(?i)addba <@!?(\\d+)>$", cmdString).get(0);

            // Check that user exists on the server
            int result = ConfigTool.addBotAdminByID(gmre.getGuild().getId(), userID, gmre.getJDA());
            if (result == 1) {
                msgOut = "Successfully added bot admin: " + gmre.getGuild().getMemberById(userID).getUser().getName();
                ConfigTool.writeConfig();
            } else if (result == -4) {
                msgOut = "Error adding bot admin.";
            } else if (result == -3) {
                msgOut = "Error adding bot admin. User is already a bot admin.";
            } else if (result == -2) {
                msgOut = "Error adding bot admin. User not found on this server.";
            } else if (result == -1) {
                msgOut = "Error adding bot admin. Server not found.";
            }
            msgSet = true;

        // Removes a bot admin for the server
        } else if (Pattern.matches("^(?i)remba <@!?\\d+>$", cmdString)) {
            String userID = RegexTool.getGroups("^(?i)remba <@!?(\\d+)>$", cmdString).get(0);

            int result = ConfigTool.removeBotAdminByID(gmre.getGuild().getId(), userID, gmre.getJDA());
            if (result == 1) {
                msgOut = "Successfully removed bot admin: " + gmre.getGuild().getMemberById(userID).getUser().getName();
                ConfigTool.writeConfig();
            } else if (result == -2) {
                msgOut = "Error removing bot admin. User is not an existing bot admin.";
            } else if (result == -1) {
                msgOut = "Error removing bot admin. Server was not found.";
            }
            msgSet = true;

        // Adds a bot channel for this server
        } else if (Pattern.matches("^(?i)addbc <#\\d+>$", cmdString)) {
            String channelID = RegexTool.getGroups("^(?i)addbc <#(\\d+)>$", cmdString).get(0);

            int result = ConfigTool.addBotChannelByID(gmre.getGuild().getId(), channelID, gmre.getJDA());
            if (result == 1) {
                msgOut = "Successfully added bot channel: " + gmre.getGuild().getTextChannelById(channelID).getName();
                ConfigTool.writeConfig();
            } else if (result == -4) {
                msgOut = "Error adding bot channel.";
            } else if (result == -3) {
                msgOut = "Error adding bot channel. Channel already bot channel.";
            } else if (result == -2) {
                msgOut = "Error adding bot channel. Channel doesn't exist in server.";
            } else if (result == -1) {
                msgOut = "Error adding bot channel. Server was not found.";
            }
            msgSet = true;

        // Removes a bot channel for this server
        } else if (Pattern.matches("^(?i)rembc <#\\d+>$", cmdString)) {
            String channelID = RegexTool.getGroups("^(?i)rembc <#(\\d+)>$", cmdString).get(0);


            int result = ConfigTool.removeBotChannelByID(gmre.getGuild().getId(), channelID, gmre.getJDA());
            if (result == 1) {
                msgOut = "Successfully removed bot channel: " + gmre.getGuild().getTextChannelById(channelID).getName();
                ConfigTool.writeConfig();
            } else if (result == -2) {
                msgOut = "Error removing bot channel. Channel is not a bot channel.";
            } else if (result == -1) {
                msgOut = "Error removing bot channel. Server was not found.";
            }
            msgSet = true;
        }

        // Queue message to send if there is one to send
        if (msgSet) {
            gmre.getChannel().sendMessage(msgOut).queue();
        }
    }
}
