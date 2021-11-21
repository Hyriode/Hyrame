package fr.hyriode.hyrame.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/09/2021 at 11:44
 */
public class HyriConfiguration {

    /** Plugin configuration */
    private final FileConfiguration configuration;

    /** Plugin */
    private final JavaPlugin plugin;

    /**
     * Constructor of {@link HyriConfiguration}
     *
     * @param plugin - {@link JavaPlugin} object
     */
    public HyriConfiguration(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configuration = this.plugin.getConfig();
    }

    /**
     * Set a given object in configuration
     *
     * @param key - Value key
     * @param value - Value to set
     * @param replace - If a value already exists it will replace it
     * @return - Value set
     */
    public Object set(String key, Object value, boolean replace) {
        if (this.configuration.get(key) == null || replace) {
            this.configuration.set(key, value);

            this.plugin.saveConfig();
        }
        return this.get(key);
    }

    /**
     * Set a given object in configuration
     *
     * @param key - Value key
     * @param value - Value to set
     * @return - Value set
     */
    public Object set(String key, Object value) {
        return this.set(key, value, false);
    }

    /**
     * Set a given object in configuration
     *
     * @param  path - Key path
     * @param key - Value key
     * @param value - Value to set
     * @param replace - If a value already exists it will replace it
     * @return - Value set
     */
    public Object set(String path, String key, Object value, boolean replace) {
        ConfigurationSection section = this.configuration.getConfigurationSection(path);

        if (section == null) {
            section = this.configuration.createSection(path);
        }

        if (section.get(key) == null || replace) {
            section.set(key, value);

            this.plugin.saveConfig();
        }
        return this.get(path, key);
    }

    /**
     * Set a given object in configuration
     *
     * @param  path - Key path
     * @param key - Value key
     * @param value - Value to set
     * @return - Value set
     */
    public Object set(String path, String key, Object value) {
        return this.set(path, key, value, false);
    }

    /**
     * Get a boolean from the configuration
     *
     * @param key - Value key
     * @return - The result
     */
    public boolean getBoolean(String key) {
        return this.configuration.getBoolean(key);
    }

    /**
     * Get a boolean from the configuration
     *
     * @param  path - Key path
     * @param key - Value key
     * @return - The result
     */
    public boolean getBoolean(String path, String key) {
        return this.configuration.getConfigurationSection(path).getBoolean(key);
    }

    /**
     * Get a double from the configuration
     *
     * @param key - Value key
     * @return - The result
     */
    public double getDouble(String key) {
        return this.configuration.getDouble(key);
    }

    /**
     * Get a double from the configuration
     *
     * @param  path - Key path
     * @param key - Value key
     * @return - The result
     */
    public double getDouble(String path, String key) {
        return this.configuration.getConfigurationSection(path).getDouble(key);
    }

    /**
     * Get an integer from the configuration
     *
     * @param key - Value key
     * @return - The result
     */
    public int getInt(String key) {
        return this.configuration.getInt(key);
    }

    /**
     * Get an integer from the configuration
     *
     * @param  path - Key path
     * @param key - Value key
     * @return - The result
     */
    public int getInt(String path, String key) {
        return this.configuration.getConfigurationSection(path).getInt(key);
    }

    /**
     * Get a long from the configuration
     *
     * @param key - Value key
     * @return - The result
     */
    public long getLong(String key) {
        return this.configuration.getLong(key);
    }

    /**
     * Get a long from the configuration
     *
     * @param  path - Key path
     * @param key - Value key
     * @return - The result
     */
    public long getLong(String path, String key) {
        return this.configuration.getConfigurationSection(path).getLong(key);
    }

    /**
     * Get an object from the configuration
     *
     * @param key - Value key
     * @return - The result
     */
    public Object get(String key) {
        return this.configuration.get(key);
    }

    /**
     * Get an object from the configuration
     *
     * @param  path - Key path
     * @param key - Value key
     * @return - The result
     */
    public Object get(String path, String key) {
        return this.configuration.getConfigurationSection(path).get(key);
    }

    /**
     * Get the file configuration object
     *
     * @return - {@link FileConfiguration} object
     */
    public FileConfiguration getConfiguration() {
        return this.configuration;
    }

}
