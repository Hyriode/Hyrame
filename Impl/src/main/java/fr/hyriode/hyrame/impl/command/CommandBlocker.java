package fr.hyriode.hyrame.impl.command;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.HyrameLogger;
import fr.hyriode.hyrame.command.ICommandBlocker;
import fr.hyriode.hyrame.reflection.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class CommandBlocker implements ICommandBlocker {

    private static final String MINECRAFT_PREFIX = "minecraft";
    private static final String BUKKIT_PREFIX = "bukkit";
    private static final String SPIGOT_PREFIX = "spigot";

    private final List<String> blockedCommands;

    private final CommandMap commandMap;

    public CommandBlocker() {
        this.commandMap = this.getCommandMap();
        this.blockedCommands = new ArrayList<>();

        this.removeCommands();
    }

    private CommandMap getCommandMap() {
        return (CommandMap) Reflection.invokeField(Bukkit.getServer(), "commandMap");
    }

    private void removeCommands() {
        HyrameLogger.log("Removing default Spigot and Minecraft commands...");

        // Minecraft
        this.addBlockedCommands(MINECRAFT_PREFIX, "me", "trigger");
        this.removeCommands(MINECRAFT_PREFIX, "tell");

        if (!HyriAPI.get().getConfig().isDevEnvironment()) {
            this.removeCommands(MINECRAFT_PREFIX, "whitelist");
        }

        // Bukkit
        this.removeCommands(BUKKIT_PREFIX, "about", "version", "ver", "icanhasbukkit");
        this.removeCommands(BUKKIT_PREFIX, "save-all", "save-off", "save-on");
        this.removeCommands(BUKKIT_PREFIX, "reload", "rl");
        this.removeCommands(BUKKIT_PREFIX, "timings");
        this.removeCommands(BUKKIT_PREFIX, "plugins", "pl");
        this.removeCommands(BUKKIT_PREFIX, "help", "?");
        this.removeCommands(BUKKIT_PREFIX, "me");
        this.removeCommands(BUKKIT_PREFIX, "trigger");

        // Spigot
        this.removeCommands(SPIGOT_PREFIX, "restart", "tps");
    }

    @Override
    public void addBlockedCommands(String prefix, String... commands) {
        for (String command : commands) {
            this.blockedCommands.add(command);
            this.blockedCommands.add(prefix + ":" + command);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void removeCommands(String prefix, String... commands) {
        final SimpleCommandMap simpleCommandMap = (SimpleCommandMap) this.commandMap;
        final Map<String, Command> knownCommands = (Map<String, Command>) Reflection.invokeField(simpleCommandMap, "knownCommands");

        if (knownCommands != null) {
            for (String command : commands) {
                if (command.equals("*")) {
                    for (String knownCommand : new HashSet<>(knownCommands.keySet())) {
                        if (knownCommand.startsWith(prefix)) {
                            knownCommands.remove(knownCommand);

                            if (knownCommands.containsKey(":")) {
                                knownCommands.remove(knownCommand.split(":")[1]);
                            }
                        }
                    }
                } else {
                    knownCommands.remove(command);
                    knownCommands.remove(prefix + ":" + command);
                }
            }
        }
    }

    @Override
    public List<String> getBlockedCommands() {
        return this.blockedCommands;
    }

}
