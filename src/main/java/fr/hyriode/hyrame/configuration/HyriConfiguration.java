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

    private final FileConfiguration configuration;
    private final JavaPlugin plugin;

    public HyriConfiguration(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configuration = this.plugin.getConfig();
    }

    public Object set(String key, Object value, boolean replace) {
        if (this.configuration.get(key) == null || replace) {
            this.configuration.set(key, value);

            this.plugin.saveConfig();
        }
        return this.get(key);
    }

    public Object set(String key, Object value) {
        return this.set(key, value, false);
    }

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

    public Object set(String path, String key, Object value) {
        return this.set(path, key, value, false);
    }

    public boolean getBoolean(String key) {
        return this.configuration.getBoolean(key);
    }


    public boolean getBoolean(String path, String key) {
        return this.configuration.getConfigurationSection(path).getBoolean(key);
    }

    public double getDouble(String key) {
        return this.configuration.getDouble(key);
    }


    public double getDouble(String path, String key) {
        return this.configuration.getConfigurationSection(path).getDouble(key);
    }

    public int getInt(String key) {
        return this.configuration.getInt(key);
    }


    public int getInt(String path, String key) {
        return this.configuration.getConfigurationSection(path).getInt(key);
    }

    public long getLong(String key) {
        return this.configuration.getLong(key);
    }


    public long getLong(String path, String key) {
        return this.configuration.getConfigurationSection(path).getLong(key);
    }

    public Object get(String key) {
        return this.configuration.get(key);
    }

    public Object get(String path, String key) {
        return this.configuration.getConfigurationSection(path).get(key);
    }

    public FileConfiguration getConfiguration() {
        return this.configuration;
    }

}
