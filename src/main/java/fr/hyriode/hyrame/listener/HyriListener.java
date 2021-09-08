package fr.hyriode.hyrame.listener;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Supplier;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 31/08/2021 at 18:15
 */
public abstract class HyriListener implements Listener {

    protected Supplier<? extends JavaPlugin> pluginSupplier;

    public void setPluginSupplier(Supplier<? extends JavaPlugin> pluginSupplier) {
        this.pluginSupplier = pluginSupplier;
    }

}
