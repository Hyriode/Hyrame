package fr.hyriode.hyrame.impl;

import fr.hyriode.hyrame.plugin.IPluginProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 16:11
 */
public class HyrameProvider implements IPluginProvider {

    private static final String PACKAGE = "fr.hyriode.hyrame";

    private final HyramePlugin plugin;

    public HyrameProvider(HyramePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public String getId() {
        return "hyrame";
    }

    @Override
    public String[] getCommandsPackages() {
        return new String[]{PACKAGE};
    }

    @Override
    public String[] getListenersPackages() {
        return new String[]{PACKAGE};
    }

    @Override
    public String[] getItemsPackages() {
        return new String[]{PACKAGE};
    }

    @Override
    public String getLanguagesPath() {
        return "/lang/";
    }

}
