package events;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.ConfigTool;

public class StartEvent extends ListenerAdapter {

    public void onReady(ReadyEvent re) {
        ConfigTool.readConfig();
        System.out.println("Config information:\n");
        System.out.print(ConfigTool.getStringAll(re.getJDA()));
        System.out.println("Ready!");
    }
}