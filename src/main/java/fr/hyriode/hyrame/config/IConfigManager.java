package fr.hyriode.hyrame.config;

import fr.hyriode.hyrame.config.handler.ConfigOptionHandler;
import fr.hyriode.hystia.api.config.IConfig;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by AstFaster
 * on 01/06/2022 at 13:33
 */
public interface IConfigManager {

    /**
     * Initialize the configuration creation process
     *
     * @param player The player related to the process
     * @param configClass The class of the configuration to create
     * @param <T> The type of the configuration
     * @return The initialized {@linkplain ConfigProcess} process
     */
    <T extends IConfig> ConfigProcess<T> initConfigProcess(Player player, Class<T> configClass);

    /**
     * Register an option handler.<br>
     * This handler will be called each time a config's option need to be set
     *
     * @param optionClass The class related to the handler
     * @param handlerClass The class of the handler
     * @param <T> The type of the config's option to handle
     */
    <T> void registerOptionHandler(Class<T> optionClass, Class<? extends ConfigOptionHandler<T>> handlerClass);

    /**
     * Unregister an option handler
     *
     * @param optionClass The class of the config's option to unregister
     */
    void unregisterOptionHandler(Class<?> optionClass);

    /**
     * Get all the registered handlers for a given class
     *
     * @return A map of {@linkplain ConfigOptionHandler option handler}
     */
    Map<Class<?>, Class<? extends ConfigOptionHandler<?>>> getHandlersClasses();

    /**
     * Get the process of a given player.<br>
     * It may return <code>null</code> if the player is not running a process
     *
     * @param player The unique id of the player
     * @return The {@linkplain ConfigProcess process} related to the player; or <code>null</code>
     */
    ConfigProcess<?> getProcess(UUID player);

    /**
     * Get all the current created processes
     *
     * @return A list of {@linkplain ConfigProcess process}
     */
    List<ConfigProcess<?>> getProcesses();

}
