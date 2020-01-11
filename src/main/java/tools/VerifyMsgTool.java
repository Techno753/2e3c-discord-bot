package tools;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;

public final class VerifyMsgTool {
    // Compare msg channel ID to server's bot channel IDs
    public static boolean isCmdChannel(GuildMessageReceivedEvent gmre) {
        String msgChannelID = gmre.getChannel().getId();
        String msgServerID = gmre.getGuild().getId();
        ArrayList<String> botChannelIDs = ConfigTool.getBotChannelsByID(msgServerID);

        return (botChannelIDs.contains(msgChannelID));
    }

    // Compare msg sender ID to server's bot admin IDs
    public static boolean isBotAdmin(GuildMessageReceivedEvent gmre) {
        String msgSenderID = gmre.getAuthor().getId();
        String msgServerID = gmre.getGuild().getId();
        ArrayList<String> botAdminIDs = ConfigTool.getBotAdminsByID(msgServerID);

        return (botAdminIDs.contains(msgSenderID));
    }

    // Compare msg sender ID to bot creator ID
    public static boolean isBotCreator(GuildMessageReceivedEvent gmre) {
        return (gmre.getAuthor().getId().equals("110731479264264192"));
    }

    // Verifies the message has the correct prefix for the server
    public static boolean hasCorrectPrefix(GuildMessageReceivedEvent gmre) {
        String msgPrefix = gmre.getMessage().getContentRaw().split("")[0];
        String serverPrefix = ConfigTool.getBotPrefixByID(gmre.getGuild().getId());

        return msgPrefix.equals(serverPrefix);
    }
}
