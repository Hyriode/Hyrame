package fr.hyriode.hyrame;

import fr.hyriode.hyrame.commands.TestCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class Hyrame extends JavaPlugin {

    public void onEnable() {
        this.registerCommands();
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
