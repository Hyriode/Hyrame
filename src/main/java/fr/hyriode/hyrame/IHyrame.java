package fr.hyriode.hyrame;

import fr.hyriode.hyrame.chat.IHyriChatManager;
import fr.hyriode.hyrame.command.IHyriCommandManager;
import fr.hyriode.hyrame.config.IConfigManager;
import fr.hyriode.hyrame.game.IHyriGameManager;
import fr.hyriode.hyrame.host.IHostController;
import fr.hyriode.hyrame.inventory.IHyriInventoryManager;
import fr.hyriode.hyrame.item.IHyriItemManager;
import fr.hyriode.hyrame.language.ILanguageLoader;
import fr.hyriode.hyrame.listener.IHyriListenerManager;
import fr.hyriode.hyrame.packet.IPacketInterceptor;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import fr.hyriode.hyrame.scanner.IHyriScanner;
import fr.hyriode.hyrame.scoreboard.IHyriScoreboardManager;
import fr.hyriode.hyrame.tablist.ITabListManager;
import fr.hyriode.hyrame.world.IWorldProvider;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

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
    Supplier<World> WORLD = () -> HyrameLoader.getHyrame().getWorldProvider().getCurrentWorld();

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
     * Get the plugin instance of {@link IHyrame}
     *
     * @return A {@link JavaPlugin}
     */
    JavaPlugin getPlugin();

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
     * Get Hyrame {@link ILanguageLoader} instance
     *
     * @return {@link ILanguageLoader} instance
     */
    ILanguageLoader getLanguageLoader();

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

    /**
     * Get the host controller instance.<br>
     * It can be used to interact with registered host.
     *
     * @return The {@link IHostController} instance
     */
    IHostController getHostController();

    /**
     * Get the world provider instance.<br>
     * It can be used to get the current world of the server or change it
     *
     * @return The {@link IWorldProvider} instance
     */
    IWorldProvider getWorldProvider();

}
