package fr.hyriode.hyrame.item;

import fr.hyriode.hyrame.plugin.IPluginProvider;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 14/11/2021 at 17:51
 */
public interface IHyriItemManager {

    /**
     * Register all items from providers
     */
    void registerItems();

    /**
     * Register all items from a given provider
     *
     * @param pluginProvider {@link IPluginProvider} object
     */
    void registerItems(IPluginProvider pluginProvider);

    /**
     * Register all items from a given provider in a given package
     *
     * @param pluginProvider {@link IPluginProvider} object
     * @param packageName Package to search
     */
    void registerItems(IPluginProvider pluginProvider, String packageName);

    /**
     * Register a given item class
     *
     * @param pluginProvider {@link IPluginProvider} object
     * @param itemClass Item's class
     */
    void registerItem(IPluginProvider pluginProvider, Class<? extends HyriItem<?>> itemClass);

    /**
     * Give an item to a player
     *
     * @param player Player
     * @param slot Item's slot
     * @param itemClass Item's class
     */
    void giveItem(Player player, int slot, Class<? extends HyriItem<?>> itemClass);

    /**
     * Give an item to a player
     *
     * @param player Player
     * @param slot Item's slot
     * @param name Item's name
     */
    void giveItem(Player player, int slot, String name);

    /**
     * Give an item to a player
     *
     * @param player Player
     * @param itemClass Item's class
     * @return <code>true</code> if an item with the given class has been found
     */
    boolean giveItem(Player player, Class<? extends HyriItem<?>> itemClass);

    /**
     * Give an item to a player
     *
     * @param player Player
     * @param name Item's name
     * @return <code>true</code> if an item with the given name has been found
     */
    boolean giveItem(Player player, String name);

    /**
     * Get an item by its class
     *
     * @param itemClass Item's class
     * @return {@link HyriItem} object
     */
    HyriItem<?> getItem(Class<? extends HyriItem<?>> itemClass);

    /**
     * Get an item by its name
     *
     * @param name Item's name
     * @return {@link HyriItem} object
     */
    HyriItem<?> getItem(String name);

    /**
     * Get all items
     *
     * @return A map of items. Key: item's id, Value: the item
     */
    Map<String, HyriItem<?>> getItems();

}
