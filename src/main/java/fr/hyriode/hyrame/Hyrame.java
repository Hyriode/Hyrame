package fr.hyriode.hyrame;

import fr.hyriode.hyrame.commands.CommandManager;
import fr.hyriode.hyrame.commands.TestCommand;
import fr.hyriode.hyrame.gamemethods.GameMethodsHandlers;
import fr.hyriode.hyrame.team.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

public class Hyrame extends JavaPlugin {

    private CommandManager commandManager;

    public void onEnable() {
        this.commandManager = new CommandManager(this);

        Bukkit.getServer().getPluginManager().registerEvents(new TeamHandler(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new GameMethodsHandlers(), this);
    }
}
