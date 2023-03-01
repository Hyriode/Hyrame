package fr.hyriode.hyrame;

import fr.hyriode.hyrame.chat.IChatManager;
import fr.hyriode.hyrame.command.ICommandManager;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.IHyriGameManager;
import fr.hyriode.hyrame.host.IHostController;
import fr.hyriode.hyrame.inventory.IHyriInventoryManager;
import fr.hyriode.hyrame.item.IItemManager;
import fr.hyriode.hyrame.language.ILanguageLoader;
import fr.hyriode.hyrame.listener.IListenerManager;
import fr.hyriode.hyrame.packet.IPacketInterceptor;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import fr.hyriode.hyrame.scanner.IHyriScanner;
import fr.hyriode.hyrame.scoreboard.IScoreboardManager;
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
     * Get the {@link IHyrame} implementation instance
     *
     * @return The {@link IHyrame} instance
     */
    static IHyrame get() {
        return HyrameLoader.getHyrame();
    }

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
     * Get Hyrame {@link IListenerManager} instance
     *
     * @return {@link IListenerManager} instance
     */
    IListenerManager getListenerManager();

    /**
     * Get Hyrame {@link ICommandManager} instance
     *
     * @return {@link ICommandManager} instance
     */
    ICommandManager getCommandManager();

    /**
     * Get Hyrame {@link IItemManager} instance
     *
     * @return {@link IItemManager} instance
     */
    IItemManager getItemManager();

    /**
     * Get Hyrame {@link IScoreboardManager} instance
     *
     * @return {@link IScoreboardManager} instance
     */
    IScoreboardManager getScoreboardManager();

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
     * Get the current game instance
     *
     * @return The {@link HyriGame} instance
     */
    default HyriGame<?> getGame() {
        return this.getGameManager().getCurrentGame();
    }

    /**
     * Get the chat manager instance
     *
     * @return The {@link IChatManager} instance
     */
    IChatManager getChatManager();

    /**
     * Get the tab list manager instance
     *
     * @return The {@link ITabListManager} instance
     */
    ITabListManager getTabListManager();

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
