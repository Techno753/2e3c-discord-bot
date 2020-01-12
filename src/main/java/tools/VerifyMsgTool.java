package tools;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;

/**
 * Checks information about a message.
 */
public final class VerifyMsgTool {
    /**
     * Verifies whether message came from a command channel
     * @param gmre The message event relating to message to check
     * @return True if message came from command channel, otherwise false.
     */
    public static boolean isCmdChannel(GuildMessageReceivedEvent gmre) {
        String msgChannelID = gmre.getChannel().getId();
        String msgServerID = gmre.getGuild().getId();
        ArrayList<String> botChannelIDs = ConfigTool.getBotChannelsByID(msgServerID);

        return (botChannelIDs.contains(msgChannelID));
    }

    /**
     * Verifies whether message came from a bot admin
     * @param gmre The message event relating to message to check
     * @return True if message came from a bot admin, otherwise false
     */
    public static boolean isBotAdmin(GuildMessageReceivedEvent gmre) {
        String msgSenderID = gmre.getAuthor().getId();
        String msgServerID = gmre.getGuild().getId();
        ArrayList<String> botAdminIDs = ConfigTool.getBotAdminsByID(msgServerID);

        return (botAdminIDs.contains(msgSenderID));
    }

    /**
     * Verifies whether message came from bot creator (User ID = 110731479264264192)
     * @param gmre The message event relating to message to check
     * @return True if message came from bot creator, otherwise false
     */
    public static boolean isBotCreator(GuildMessageReceivedEvent gmre) {
        return (gmre.getAuthor().getId().equals("110731479264264192"));
    }

    /**
     * Verifies whether message came from server owner
     * @param gmre The message event relating to message to check
     * @return True if message came from server owner, otherwise false
     */
    public static boolean isServerOwner(GuildMessageReceivedEvent gmre) {
        String ownerID = gmre.getGuild().getOwnerId();
        String userID = gmre.getAuthor().getId();

        return ownerID.equals(userID);
    }

    /**
     * Verifies whether the message has the correct prefix for the server
     * @param gmre The message event relating to message to check
     * @return True if message has correct prefix, otherwise false
     */
    public static boolean hasCorrectPrefix(GuildMessageReceivedEvent gmre) {
        String msgPrefix = gmre.getMessage().getContentRaw().split("")[0];
        String serverPrefix = ConfigTool.getBotPrefixByID(gmre.getGuild().getId());

        return msgPrefix.equals(serverPrefix);
    }
}
