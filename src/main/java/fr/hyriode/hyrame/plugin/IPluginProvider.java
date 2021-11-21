package fr.hyriode.hyrame.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 31/08/2021 at 16:55
 */
public interface IPluginProvider {

    /**
     * Get plugin main class
     *
     * @return - A {@link JavaPlugin}
     */
    JavaPlugin getPlugin();

    /**
     * Get provider id used for items, etc.
     * For example: hyrirtf. Items will render as hyrirtf:my_item
     *
     * @return - Provider's id
     */
    String getId();

    /**
     * Get located packages of commands to register
     *
     * @return - A list of {@link String}
     */
    String[] getCommandsPackages();

    /**
     * Get located packages of listeners to register
     *
     * @return - A list of {@link String}
     */
    String[] getListenersPackages();

    /**
     * Get located packages of items to register
     *
     * @return - A list of {@link String}
     */
    String[] getItemsPackages();

    /**
     * Get the languages path
     *
      * @return - Languages path
     */
    String getLanguagesPath();

    /**
     * Get plugin logger
     *
     * @return - {@link Logger}
     */
    default Logger getLogger() {
        return this.getPlugin().getLogger();
    }

}
