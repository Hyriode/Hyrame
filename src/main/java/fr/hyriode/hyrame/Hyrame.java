package fr.hyriode.hyrame;

import fr.hyriode.hyrame.commands.TestCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Hyrame extends JavaPlugin {

    public void onEnable() {
        this.getCommand("test").setExecutor(new TestCommand());
    }
}
