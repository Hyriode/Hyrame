package fr.hyriode.hyrame;

import fr.hyriode.hyrame.plugin.IPluginProvider;

import java.util.logging.Level;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class HyrameLoader {

    /** {@link IHyrame} instance */
    private static IHyrame hyrame;

    /**
     * Load a plugin who wants to use Hyrame by giving its {@link IPluginProvider}
     *
     * @param pluginProvider - {@link IPluginProvider} to load
     * @return - {@link IHyrame} instance
     */
    public static IHyrame load(IPluginProvider pluginProvider) {
        if (hyrame == null) {
            throw new IllegalStateException("No implementation of " + IHyrame.NAME + " has been registered! Check your runtime and register configurations!");
        }

        if (hyrame.isLoaded(pluginProvider)) {
            throw new IllegalStateException("A plugin provider for '" + pluginProvider.getClass().getName() + "' is already registered!");
        }

        hyrame.load(pluginProvider);

        return hyrame;
    }

    /**
     * Register an implementation of Hyrame
     *
     * @param hyrame - New {@link IHyrame} implementation
     */
    public static void register(IHyrame hyrame) {
        HyrameLoader.hyrame = hyrame;

        HyrameLoader.hyrame.getLogger().log(Level.INFO, "Registered '" + hyrame.getClass().getName() + "' as an implementation of " + IHyrame.NAME + ".");
    }

}
