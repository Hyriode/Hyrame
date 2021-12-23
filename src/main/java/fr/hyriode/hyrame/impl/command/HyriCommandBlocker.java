package fr.hyriode.hyrame.impl.command;

import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.tools.reflection.Reflection;
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
public class HyriCommandBlocker {

    private static final String MINECRAFT_PREFIX = "minecraft";
    private static final String BUKKIT_PREFIX = "bukkit";
    private static final String SPIGOT_PREFIX = "spigot";

    private final List<String> blockedCommands;

    private final CommandMap commandMap;

    public HyriCommandBlocker() {
        this.commandMap = this.getCommandMap();
        this.blockedCommands = new ArrayList<>();

        this.removeCommands();
    }

    private CommandMap getCommandMap() {
        return (CommandMap) Reflection.invokeField(Bukkit.getServer(), "commandMap");
    }

    private void removeCommands() {
        Hyrame.log("Removing default Spigot and Minecraft commands...");

        // Minecraft
        this.addBlockedCommand(MINECRAFT_PREFIX, "tell", "me", "trigger");

        // Bukkit
        this.removeCommand(BUKKIT_PREFIX, "about", "version", "ver", "icanhasbukkit");
        this.removeCommand(BUKKIT_PREFIX, "save-all", "save-off", "save-on");
        this.removeCommand(BUKKIT_PREFIX, "reload", "rl");
        this.removeCommand(BUKKIT_PREFIX, "timings");
        this.removeCommand(BUKKIT_PREFIX, "plugins", "pl");
        this.removeCommand(BUKKIT_PREFIX, "help", "?");
        this.removeCommand(BUKKIT_PREFIX, "me");
        this.removeCommand(BUKKIT_PREFIX, "trigger");

        // Spigot
        this.removeCommand(SPIGOT_PREFIX, "restart", "tps");
    }

    private void addBlockedCommand(String prefix, String... commands) {
        for (String command : commands) {
            this.blockedCommands.add(command);
            this.blockedCommands.add(prefix + ":" + command);
        }
    }

    @SuppressWarnings("unchecked")
    private void removeCommand(String prefix, String... commands) {
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

    public List<String> getBlockedCommands() {
        return this.blockedCommands;
    }

}
