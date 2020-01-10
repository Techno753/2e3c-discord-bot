import events.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import tools.ConfigTool;

public class DiscordBot {
    public static void main(String[] args) throws Exception{
        // Read Config file if exists
        ConfigTool.readConfig();

        // Initialise connection to Discord
        JDA jda = new JDABuilder("NjY0NjIxOTUxMDM3NDcyNzk4.XhgkIQ.Jn2stxKYpdeEuuP6nFQ_OrpKdlw").build();

        // User Events
        jda.addEventListener(new GenericEvent());
        jda.addEventListener(new RNGEvent());
        jda.addEventListener(new ServerInfoEvent());

        // Elevated Events
        jda.addEventListener(new ServerConfigEvent());

        // DEBUG Register Test Event
        jda.addEventListener(new TestEvent());




    }
}
