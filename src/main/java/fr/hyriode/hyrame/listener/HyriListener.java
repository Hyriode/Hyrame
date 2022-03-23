package fr.hyriode.hyrame.listener;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 17:02
 */
public abstract class HyriListener<T extends JavaPlugin> implements Listener {

    /** Listener's plugin */
    protected final T plugin;

    /**
     * Constructor of {@link HyriListener}
     *
     * @param plugin Listener's plugin
     */
    public HyriListener(T plugin) {
        this.plugin = plugin;
    }

}
