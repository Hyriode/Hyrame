package fr.hyriode.hyrame.item;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 14/11/2021 at 09:04
 */
public abstract class HyriItem<T extends JavaPlugin> {

    /** Spigot plugin */
    protected final T plugin;

    /** Item name/id. Example: my_item */
    protected final String name;

    /** Item's display name */
    protected Supplier<HyriLanguageMessage> displayName;

    /** Item's description */
    protected Supplier<List<HyriLanguageMessage>> description;

    /** Item's material */
    protected Material material;

    /** Item's data */
    protected byte data;

    /**
     * Constructor of {@link HyriItem}
     *
     * @param plugin Spigot plugin
     * @param name Item's name
     * @param displayName Item's display name
     * @param description Item's description
     * @param material Item's material
     * @param data Item's data
     */
    public HyriItem(T plugin, String name, Supplier<HyriLanguageMessage> displayName, Supplier<List<HyriLanguageMessage>> description, Material material, byte data) {
        this.plugin = plugin;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.material = material;
        this.data = data;
    }

    /**
     * Constructor of {@link HyriItem}
     *
     * @param plugin Spigot plugin
     * @param name Item's name
     * @param displayName Item's display name
     * @param description Item's description
     * @param material Item's material
     */
    public HyriItem(T plugin, String name, Supplier<HyriLanguageMessage> displayName, Supplier<List<HyriLanguageMessage>> description, Material material) {
        this(plugin, name, displayName, description, material, (byte) 0);
    }

    /**
     * Constructor of {@link HyriItem}
     *
     * @param plugin Spigot plugin
     * @param name Item's name
     * @param displayName Item's display name
     * @param material Item's material
     * @param data Item's data
     */
    public HyriItem(T plugin, String name, Supplier<HyriLanguageMessage> displayName, Material material, byte data) {
        this(plugin, name, displayName, ArrayList::new, material, data);
    }

    /**
     * Constructor of {@link HyriItem}
     *
     * @param plugin Spigot plugin
     * @param name Item's name
     * @param displayName Item's display name
     * @param material Item's material
     */
    public HyriItem(T plugin, String name, Supplier<HyriLanguageMessage> displayName, Material material) {
        this(plugin, name, displayName, ArrayList::new, material, (byte) 0);
    }

    /**
     * Called before giving the item to the player
     *
     * @param hyrame {@link IHyrame} instance
     * @param player The player that will receive the item
     * @param slot The slot where the item will be set
     * @param itemStack The item stack that will be set
     * @return A modified {@link ItemStack} or by default, the auto-created {@link ItemStack}
     */
    public ItemStack onPreGive(IHyrame hyrame, Player player, int slot, ItemStack itemStack) {
        return itemStack;
    }

    /**
     * Called when this item is given to a player
     *
     * @param hyrame {@link IHyrame} instance
     * @param player Player with the item
     * @param slot Slot
     * @param itemStack {@link ItemStack}
     */
    public void onGive(IHyrame hyrame, Player player, int slot, ItemStack itemStack) {}

    /**
     * Called when a player left click with this item
     *
     * @param hyrame {@link IHyrame} instance
     * @param event Event fired
     */
    public void onLeftClick(IHyrame hyrame, PlayerInteractEvent event) {}

    /**
     * Called when a player right click with this item
     *
     * @param hyrame {@link IHyrame} instance
     * @param event Event fired
     */
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {}

    /**
     * Called when a player click on the item in his inventory
     *
     * @param hyrame {@link IHyrame} instance
     * @param event The event fired
     */
    public void onInventoryClick(IHyrame hyrame, InventoryClickEvent event) {}

    /**
     * Get item's name
     *
     * @return Item's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get item's display name
     *
     * @return Item's display name
     */
    public Supplier<HyriLanguageMessage> getDisplayName() {
        return this.displayName;
    }

    /**
     * Get item's description
     *
     * @return - A list of description lines
     */
    public Supplier<List<HyriLanguageMessage>> getDescription() {
        return this.description;
    }

    /**
     * Get item's material
     *
     * @return Item's material
     */
    public Material getMaterial() {
        return this.material;
    }

    /**
     * Get item's data
     *
     * @return Item's data
     */
    public byte getData() {
        return this.data;
    }

}
