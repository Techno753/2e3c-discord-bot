package events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TestEvent extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent gmre) {
        // Prints any messages the bot sees to the terminal
        System.out.println(gmre.getMessage().getContentRaw());
    }
}
