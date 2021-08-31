package fr.hyriode.hyrame.commands;

import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class HyriCommand extends Command {

    protected JavaPlugin plugin;


    protected HyriCommand(String name) {
        super(name);
    }

    protected HyriCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }
}
