package fr.hyriode.hyrame;

import fr.hyriode.hyrame.chat.IHyriChatManager;
import fr.hyriode.hyrame.command.IHyriCommandManager;
import fr.hyriode.hyrame.config.IConfigManager;
import fr.hyriode.hyrame.game.IHyriGameManager;
import fr.hyriode.hyrame.inventory.IHyriInventoryManager;
import fr.hyriode.hyrame.item.IHyriItemManager;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.hyrame.listener.IHyriListenerManager;
import fr.hyriode.hyrame.packet.IPacketInterceptor;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import fr.hyriode.hyrame.scanner.IHyriScanner;
import fr.hyriode.hyrame.scoreboard.IHyriScoreboardManager;
import fr.hyriode.hyrame.tablist.ITabListManager;
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
     * Get Hyrame {@link IHyriScoreboardManager} instance
     *
     * @return {@link IHyriScoreboardManager} instance
     */
    IHyriScoreboardManager getScoreboardManager();

    /**
     * Get Hyrame {@link IHyriInventoryManager} instance
     *
     * @return The {@link IHyriInventoryManager} instance
     */
    IHyriInventoryManager getInventoryManager();

    /**
     * Get Hyrame {@link IHyriGameManager} instance
     *
     * @return {@link IHyriGameManager} instance
     */
    IHyriGameManager getGameManager();

    /**
     * Get the chat manager instance
     *
     * @return The {@link IHyriChatManager} instance
     */
    IHyriChatManager getChatManager();

    /**
     * Get the tab list manager instance
     *
     * @return The {@link ITabListManager} instance
     */
    ITabListManager getTabListManager();

    /**
     * Get the configuration manager instance.<br>
     * This class contains all config's creations process-related-methods.
     *
     * @return The {@link IConfigManager} instance
     */
    IConfigManager getConfigManager();

    /**
     * Get the packet interceptor instance.<br>
     * It can be used to check sending and received packet.
     *
     * @return The {@link IPacketInterceptor} instance
     */
    IPacketInterceptor getPacketInterceptor();

}
