package fr.hyriode.hyrame.listener;

import fr.hyriode.hyrame.plugin.IPluginProvider;

import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 17:04
 */
public interface IListenerManager {

    /**
     * Register all listeners in packages provided by plugin providers
     */
    void registerListeners();

    /**
     * Register all listeners in packages provided by {@link IPluginProvider}
     *
     * @param pluginProvider - {@link IPluginProvider} with listeners packages
     */
    void registerListeners(IPluginProvider pluginProvider);

    /**
     * Register all listeners in package provided
     *
     * @param pluginProvider - {@link IPluginProvider} used to search listeners
     * @param packageName - Package to search
     */
    void registerListeners(IPluginProvider pluginProvider, String packageName);

    /**
     * Get a listener from its class
     *
     * @param listenerClass The class of the listener
     * @param <T> A type
     * @return A {@link  HyriListener}
     */
    <T extends HyriListener<?>> T getListener(Class<T> listenerClass);

    /**
     * Get all listeners registered by Hyrame
     *
     * @return A list of {@link  HyriListener}
     */
    List<HyriListener<?>> getListeners();

}
