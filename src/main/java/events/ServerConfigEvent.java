package events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.ConfigTool;


/*
 * Event Listener for Server related events
 */
public class ServerConfigEvent extends ListenerAdapter {
    EmbedBuilder eb = new EmbedBuilder();

    // Adds server to server configs
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
        System.out.println(msgIn);
        String msgOut = "";
        Boolean msgSet = false;

        switch (msgIn.toLowerCase().split("")[0]) {
            // Displays info of servers the bot is in. BC.
            case "servers":
                msgOut = ConfigTool.getString();

                msgSet = true;
                break;

            // Sets the command prefix for the server. BA, BC.
            case "prefix":

        }

        System.out.println(msgOut);

        if (msgSet) {
            gmre.getChannel().sendMessage(msgOut).queue();
        }

        msgSet = false;
        eb.clear();
    }
}
