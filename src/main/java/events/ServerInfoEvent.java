package events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.ConfigTool;
import tools.VerifyMsgTool;

import java.util.regex.Pattern;

import static tools.VerifyMsgTool.hasPrivs;
import static tools.VerifyMsgTool.isCmdChannel;


/**
 * Displays server and channel information
 */
public class ServerInfoEvent extends ListenerAdapter {
    EmbedBuilder eb = new EmbedBuilder();

    /**
     * Displays server and channel information
     * @param gmre Message event
     */
    public void onGuildMessageReceived(GuildMessageReceivedEvent gmre) {
        // Get message as raw String
        String msgIn = gmre.getMessage().getContentRaw();
        String cmdString = "";
        String msgOut = "";
        boolean msgSet = false;

        if (msgIn.length() > 1 && VerifyMsgTool.hasCorrectPrefix(gmre)) {
            cmdString = msgIn.toLowerCase().substring(1);
        }

        // Gets channel information
        if (Pattern.matches("^(?i)cinfo$", cmdString) &&
                isCmdChannel(gmre)) {
            try {
                eb.setTitle("Channel Information");
                eb.addField("Channel Name", gmre.getChannel().getName(), true);
//                eb.addField("Channel Topic", gmre.getChannel().getTopic(), true);
                eb.addField("Channel Topic", (gmre.getChannel().getTopic() != null) ? gmre.getChannel().getTopic() : "No Topic", true);
                eb.addField("Is NSFW", Boolean.toString(gmre.getChannel().isNSFW()), true);
                eb.addField("Channel ID", gmre.getChannel().getId(), true);
                eb.setThumbnail(gmre.getGuild().getIconUrl());

                msgSet = true;
            } catch (Exception e) {
                System.out.println("Failed to obtain channel information.");
                e.printStackTrace();
            }

        // Gets server information
        } else if (Pattern.matches("^(?i)sinfo$", cmdString) &&
                isCmdChannel(gmre)) {
            try {
                eb.setTitle("Server Information");
                eb.addField("Server Name", gmre.getGuild().getName(), true);
                eb.addField("Server ID", gmre.getGuild().getId(), true);
                eb.setThumbnail(gmre.getGuild().getIconUrl());

                msgSet = true;
            } catch (Exception e) {
                System.out.println("Failed to obtain server information.");
            }
        }

        // Displays message
        if (msgSet) {
            gmre.getChannel().sendMessage(eb.build()).queue();
        }
        eb.clear();
    }
}
