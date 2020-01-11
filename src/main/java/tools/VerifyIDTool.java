package tools;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;

import java.nio.channels.Channel;

// Verifies various IDs
public final class VerifyIDTool {

    /**
     * Verifies whether the bot is in a server with the given server ID
     * @param serverID Server ID to check if the bot is a member of
     * @param jda JDA object to access the servers the bot is in
     * @return 1 - The bot is in the given server
     *          -1 - The bot is not in the given server
     */
    public static int verifyInServerByServerID(String serverID, JDA jda) {
        for (Guild g : jda.getGuilds()) {
            if (g.getId().equals(serverID)) {
                return 1;
            }
        }
        return -1;
    }

    /**
     * Verifies whether a given User is in a given server.
     * @param serverID The server ID to check.
     * @param userID The user ID to check.
     * @param jda The JDA object to access server information.
     * @return 1 - The given user is in the given server.
     *          -1 - The given user is not in the given server.
     */
    public static int verifyUserInServer(String serverID, String userID, JDA jda) {
        for (Member mem : jda.getGuildById(serverID).getMembers()) {
            if (mem.getId().equals(userID)) {
                return 1;   // User in server
            }
        }
        return -1;  // User doesn't exist in server
    }

    public static int verifyChannelInServer(String serverID, String channelID, JDA jda) {
        for (GuildChannel gc : jda.getGuildById(serverID).getChannels()) {
            if (gc.getId().equals(channelID)) {
                return 1;   // Channel in server
            }
        }
        return -1;  // Channel doesn't exist in server
    }
}
