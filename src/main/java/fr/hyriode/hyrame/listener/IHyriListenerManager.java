package fr.hyriode.hyrame.listener;

import fr.hyriode.hyrame.plugin.IPluginProvider;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 17:04
 */
public interface IHyriListenerManager {

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

}