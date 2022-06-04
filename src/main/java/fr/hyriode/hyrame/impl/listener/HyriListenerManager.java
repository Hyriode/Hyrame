package fr.hyriode.hyrame.impl.listener;

import fr.hyriode.hyrame.HyrameLogger;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyrame.listener.IHyriListenerManager;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import fr.hyriode.hyrame.reflection.Reflection;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 17:25
 */
public class HyriListenerManager implements IHyriListenerManager {

    private final Map<Class<?>, HyriListener<?>> listeners;

    private final Hyrame hyrame;

    public HyriListenerManager(Hyrame hyrame) {
        this.hyrame = hyrame;
        this.listeners = new HashMap<>();
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

        HyrameLogger.log("Searching for listeners in '" + packageName + "' package" + formattedPluginProviderName);

        final Set<Class<?>> classes = this.hyrame.getScanner().scan(pluginProvider.getClass().getClassLoader(), packageName);

        try {
            for (Class<?> clazz : classes) {
                if (Reflection.inheritOf(clazz, HyriListener.class)) {
                    final Class<?> pluginClass = pluginProvider.getPlugin().getClass();

                    if (Reflection.hasConstructorWithParameters(clazz, pluginClass)) {
                        final HyriListener<?> listener = (HyriListener<?>) clazz.getConstructor(pluginClass).newInstance(pluginProvider.getPlugin());
                        final JavaPlugin plugin = this.hyrame.getPlugin();

                        plugin.getServer().getPluginManager().registerEvents(listener, plugin);

                        this.listeners.put(listener.getClass(), listener);

                        HyrameLogger.log("Registered '" + clazz.getName() + "' listener" + formattedPluginProviderName);
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T extends HyriListener<?>> T getListener(Class<T> listenerClass) {
        return listenerClass.cast(this.listeners.get(listenerClass));
    }

    @Override
    public List<HyriListener<?>> getListeners() {
        return new ArrayList<>(this.listeners.values());
    }

}
