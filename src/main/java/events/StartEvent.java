package events;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.ConfigTool;

/**
 * Code to execute upon the bot readying
 */
public class StartEvent extends ListenerAdapter {

    public void onReady(ReadyEvent re) {
        ConfigTool.readConfig();
        ConfigTool.updateServers(re.getJDA());
        ConfigTool.printConfig(re.getJDA());
        System.out.println("Ready!");
    }
}