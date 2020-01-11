import events.*;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import tools.ConfigTool;

public class DiscordBot {
    public static void main(String[] args) throws Exception{

        // Read Config file if exists


        // Initialise bot and connection to discord
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken("NjY0NjIxOTUxMDM3NDcyNzk4.Xhkgvg.Mrqw2AJgrssehVImnOMkxNswots");

        builder.addEventListeners(new StartEvent(),
                new GenericEvent(),
                new RNGEvent(),
                new ServerInfoEvent(),

                new ServerConfigEvent(),

                new TestEvent());

        JDA jda = builder.build();

        ConfigTool.getStringAll(jda);
    }
}
