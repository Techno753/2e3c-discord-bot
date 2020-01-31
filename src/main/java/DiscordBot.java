import events.*;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import tools.ConfigTool;
import tools.InputTool;
import tools.YTTool;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DiscordBot {
    public static void main(String[] args) throws Exception{

        // Read Config file if exists


        // Initialise bot and connection to discord
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        String discToken = InputTool.inputFromTerminal("Enter Discord token: ");
        builder.setToken(discToken);
        String GoogleKey = InputTool.inputFromTerminal("Enter Google API key: ");
        YTTool.setAPIKey(GoogleKey);

        builder.addEventListeners(new StartEvent(),
                new GenericEvent(),
                new RNGEvent(),
                new TagEvent(),
                new ServerInfoEvent(),
                new AudioEvent(),

                new ServerConfigEvent(),

                new TestEvent());

        JDA jda = builder.build();

        ConfigTool.getStringAll(jda);


    }
}
