package fr.hyriode.hyrame;

import fr.hyriode.hyrame.commands.TestCommand;
import fr.hyriode.hyrame.team.NoFireFriendTeam;
import fr.hyriode.hyrame.team.TeamSelector;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandMap;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class Hyrame extends JavaPlugin {

    public void onEnable() {
        this.registerCommands();

        Bukkit.getServer().getPluginManager().registerEvents(new NoFireFriendTeam(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new TeamSelector(), this);
    }

    private void registerCommands() {
        CommandMap commandMap = null;
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
        } catch(Exception e) {
            e.printStackTrace();
        }

        if(commandMap != null) {
            commandMap.register("test", new TestCommand());
        }
    }
}
