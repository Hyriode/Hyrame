package fr.hyriode.hyrame;

import fr.hyriode.hyrame.chat.IHyriChatHandler;
import fr.hyriode.hyrame.command.IHyriCommandManager;
import fr.hyriode.hyrame.game.IHyriGameManager;
import fr.hyriode.hyrame.item.IHyriItemManager;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.hyrame.listener.IHyriListenerManager;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import fr.hyriode.hyrame.scanner.IHyriScanner;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.function.Supplier;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public interface IHyrame {

    /** Hyrame name constant */
    String NAME = "Hyrame";
    /** Bukkit world constant */
    Supplier<World> WORLD = () -> Bukkit.getWorld("world");

    /**
     * Load a plugin who wants to use Hyrame by giving its {@link IPluginProvider}
     *
     * @param pluginProvider {@link IPluginProvider} to load
     */
    void load(IPluginProvider pluginProvider);

    /**
     * Check if a plugin provider if loaded
     *
     * @param pluginProvider {@link IPluginProvider} to check
     * @return <code>true</code> if is loaded
     */
    boolean isLoaded(IPluginProvider pluginProvider);

    /**
     * Get Hyrame configuration
     *
     * @return {@link IHyrameConfiguration} instance
     */
    IHyrameConfiguration getConfiguration();

    /**
     * Get Hyrame {@link IHyriScanner} instance
     *
     * @return {@link IHyriScanner} instance
     */
    IHyriScanner getScanner();

    /**
     * Get Hyrame {@link IHyriLanguageManager} instance
     *
     * @return {@link IHyriLanguageManager} instance
     */
    IHyriLanguageManager getLanguageManager();

    /**
     * Get Hyrame {@link IHyriListenerManager} instance
     *
     * @return {@link IHyriListenerManager} instance
     */
    IHyriListenerManager getListenerManager();

    /**
     * Get Hyrame {@link IHyriCommandManager} instance
     *
     * @return {@link IHyriCommandManager} instance
     */
    IHyriCommandManager getCommandManager();

    /**
     * Get Hyrame {@link IHyriItemManager} instance
     *
     * @return {@link IHyriItemManager} instance
     */
    IHyriItemManager getItemManager();

    /**
     * Get Hyrame {@link IHyriGameManager} instance
     *
     * @return {@link IHyriGameManager} instance
     */
    IHyriGameManager getGameManager();

    /**
     * Get current {@link IHyriChatHandler}
     *
     * @return {@link IHyriChatHandler} object
     */
    IHyriChatHandler getChatHandler();

    /**
     * Set current {@link IHyriChatHandler}
     *
     * @param chatHandler New {@link IHyriChatHandler} object
     */
    void setChatHandler(IHyriChatHandler chatHandler);

}
