package fr.hyriode.hyrame.configuration;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 31/12/2021 at 23:03
 */
public interface IHyriConfiguration {

    /**
     * Load the configuration
     */
    void load();

    /**
     * Save the configuration
     */
    void save();

    /**
     * Get the Spigot {@link FileConfiguration}
     *
     * @return {@link FileConfiguration} instance
     */
    FileConfiguration getConfig();

}
