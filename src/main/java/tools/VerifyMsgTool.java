package tools;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class VerifyMsgTool {
    public boolean isCmdChannel(GuildMessageReceivedEvent gmre) {
        return true;
    }

    public boolean isBotAdmin(GuildMessageReceivedEvent gmre) {
        return true;
    }

    public boolean isBotCreator(GuildMessageReceivedEvent gmre) {
        return true;
    }
}
