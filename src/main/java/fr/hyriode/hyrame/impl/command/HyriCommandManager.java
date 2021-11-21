package fr.hyriode.hyrame.impl.command;

import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.IHyriCommandManager;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import fr.hyriode.tools.reflection.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.logging.Level;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 19:59
 */
public class HyriCommandManager implements IHyriCommandManager {

    private final CommandMap commandMap;

    private final Hyrame hyrame;

    public HyriCommandManager(Hyrame hyrame) {
        this.hyrame = hyrame;
        this.commandMap = (CommandMap) Reflection.invokeField(Bukkit.getServer(), "commandMap");
    }

    @Override
    public void registerCommands() {
        for (IPluginProvider pluginProvider : this.hyrame.getPluginProviders()) {
            this.registerCommands(pluginProvider);
        }
    }

    @Override
    public void registerCommands(IPluginProvider pluginProvider) {
        for (String packageName : pluginProvider.getCommandsPackages()) {
            this.registerCommands(pluginProvider, packageName);
        }
    }

    @Override
    public void registerCommands(IPluginProvider pluginProvider, String packageName) {
        final String formattedPluginProviderName = Hyrame.formatPluginProviderName(pluginProvider);

        Hyrame.log("Searching for commands in '" + packageName + "' package" + formattedPluginProviderName);

        final Set<Class<?>> classes = this.hyrame.getScanner().scan(pluginProvider.getClass().getClassLoader(), packageName);

        try {
            for (Class<?> clazz : classes) {
                if (Reflection.inheritOf(clazz, HyriCommand.class)) {
                    final Class<?> pluginClass = pluginProvider.getPlugin().getClass();

                    if (Reflection.hasConstructorWithParameters(clazz, pluginClass)) {
                        final HyriCommand<?> command = (HyriCommand<?>) clazz.getConstructor(pluginClass).newInstance(pluginProvider.getPlugin());

                        this.commandMap.register(COMMANDS_PREFIX, command);

                        Hyrame.log("Registered '" + clazz.getName() + "' command with name '" + command.getName() + "'" + formattedPluginProviderName);
                    } else {
                        Hyrame.log(Level.WARNING, ChatColor.RED + "'" + clazz.getName() + "' command plugin type is not the same as the provided one in plugin provider!" + formattedPluginProviderName);
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
