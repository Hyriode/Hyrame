package fr.hyriode.hyrame.command;

import fr.hyriode.hyrame.plugin.IPluginProvider;

import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 19:57
 */
public interface IHyriCommandManager {

    /** Commands prefix */
    String COMMANDS_PREFIX = "hyricommand";

    /**
     * Register all commands in packages provided by plugin providers
     */
    void registerCommands();

    /**
     * Register all commands in packages provided by {@link IPluginProvider}
     *
     * @param pluginProvider - {@link IPluginProvider} with commands packages
     */
    void registerCommands(IPluginProvider pluginProvider);

    /**
     * Register all commands in package provided
     *
     * @param pluginProvider - {@link IPluginProvider} used to search commands
     * @param packageName - Package to search
     */
    void registerCommands(IPluginProvider pluginProvider, String packageName);

    /**
     * Get a command from its class
     *
     * @param commandClass The class of the command
     * @param <T> A type
     * @return A {@link  HyriCommand}
     */
    <T extends HyriCommand<?>> T getCommand(Class<T> commandClass);

    /**
     * Get all commands registered by Hyrame
     *
     * @return A list of {@link  HyriCommand}
     */
    List<HyriCommand<?>> getCommands();

    /**
     * Get the instance of the command blocker
     *
     * @return The {@link ICommandBlocker} instance
     */
    ICommandBlocker getCommandBlocker();

}
