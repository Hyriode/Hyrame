package fr.hyriode.hyrame;

import fr.hyriode.hyrame.command.IHyriCommandManager;
import fr.hyriode.hyrame.game.IHyriGameManager;
import fr.hyriode.hyrame.item.IHyriItemManager;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.hyrame.listener.IHyriListenerManager;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import fr.hyriode.hyrame.scanner.IHyriScanner;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public interface IHyrame {

    /** Hyrame name constant */
    String NAME = "Hyrame";

    /**
     * Load a plugin who wants to use Hyrame by giving its {@link IPluginProvider}
     *
     * @param pluginProvider - {@link IPluginProvider} to load
     */
    void load(IPluginProvider pluginProvider);

    /**
     * Check if a plugin provider if loaded
     *
     * @param pluginProvider - {@link IPluginProvider} to check
     * @return - <code>true</code> if is loaded
     */
    boolean isLoaded(IPluginProvider pluginProvider);

    /**
     * Print a message in the console
     *
     * @param message - Message to print
     */
    default void info(String message) {
        this.getLogger().log(Level.INFO, message);
    }

    /**
     * Get Hyrame {@link Logger} to print info linked with Hyrame
     *
     * @return - {@link Logger} instance
     */
    Logger getLogger();

    /**
     * Get Hyrame {@link IHyriScanner} instance
     *
     * @return - {@link IHyriScanner} instance
     */
    IHyriScanner getScanner();

    /**
     * Get Hyrame {@link IHyriLanguageManager} instance
     *
     * @return - {@link IHyriLanguageManager} instance
     */
    IHyriLanguageManager getLanguageManager();

    /**
     * Get Hyrame {@link IHyriListenerManager} instance
     *
     * @return - {@link IHyriListenerManager} instance
     */
    IHyriListenerManager getListenerManager();

    /**
     * Get Hyrame {@link IHyriCommandManager} instance
     *
     * @return - {@link IHyriCommandManager} instance
     */
    IHyriCommandManager getCommandManager();

    /**
     * Get Hyrame {@link IHyriItemManager} instance
     *
     * @return - {@link IHyriItemManager} instance
     */
    IHyriItemManager getItemManager();

    /**
     * Get Hyrame {@link IHyriGameManager} instance
     *
     * @return - {@link IHyriGameManager} instance
     */
    IHyriGameManager getGameManager();

}
