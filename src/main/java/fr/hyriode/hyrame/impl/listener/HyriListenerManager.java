package fr.hyriode.hyrame.impl.listener;

import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyrame.listener.IHyriListenerManager;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import fr.hyriode.tools.reflection.Reflection;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.logging.Level;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 17:25
 */
public class HyriListenerManager implements IHyriListenerManager {

    private final Hyrame hyrame;

    public HyriListenerManager(Hyrame hyrame) {
        this.hyrame = hyrame;
    }

    @Override
    public void registerListeners() {
        for (IPluginProvider pluginProvider : this.hyrame.getPluginProviders()) {
            this.registerListeners(pluginProvider);
        }
    }

    @Override
    public void registerListeners(IPluginProvider pluginProvider) {
        for (String packageName : pluginProvider.getListenersPackages()) {
            this.registerListeners(pluginProvider, packageName);
        }
    }

    @Override
    public void registerListeners(IPluginProvider pluginProvider, String packageName) {
        final String formattedPluginProviderName = Hyrame.formatPluginProviderName(pluginProvider);

        Hyrame.log("Searching for listeners in '" + packageName + "' package" + formattedPluginProviderName);

        final Set<Class<?>> classes = this.hyrame.getScanner().scan(pluginProvider.getClass().getClassLoader(), packageName);

        try {
            for (Class<?> clazz : classes) {
                if (Reflection.inheritOf(clazz, HyriListener.class)) {
                    final Class<?> pluginClass = pluginProvider.getPlugin().getClass();

                    if (Reflection.hasConstructorWithParameters(clazz, pluginClass)) {
                        final HyriListener<?> listener = (HyriListener<?>) clazz.getConstructor(pluginClass).newInstance(pluginProvider.getPlugin());
                        final JavaPlugin plugin = this.hyrame.getPlugin();

                        plugin.getServer().getPluginManager().registerEvents(listener, plugin);

                        Hyrame.log("Registered '" + clazz.getName() + "' listener" + formattedPluginProviderName);
                    } else {
                        Hyrame.log(Level.WARNING, "'" + clazz.getName() + "' listener plugin type is not the same as the provided one in plugin provider!" + formattedPluginProviderName);
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
