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
     * Get located packages of commands
     *
     * @return - A list of {@link String}
     */
    String[] getCommandsPackages();

    /**
     * Get located packages of listeners
     *
     * @return - A list of {@link String}
     */
    String[] getListenersPackages();

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
