package fr.hyriode.hyrame.item;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.IHyrame;
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
    /** The display of the item */
    protected Supplier<HyriLanguageMessage> display;
    /** The description of the item */
    protected Supplier<HyriLanguageMessage> description;
    /** The bukkit item */
    protected ItemStack item;

    /**
     * Constructor of a {@link HyriItem}
     *
     * @param plugin The plugin instance
     * @param name The name of the item
     * @param display The display of the item
     * @param description The description of the item
     * @param item The bukkit item
     */
    public HyriItem(T plugin, String name, Supplier<HyriLanguageMessage> display, Supplier<HyriLanguageMessage> description, ItemStack item) {
        this.plugin = plugin;
        this.name = name;
        this.display = display;
        this.description = description;
        this.item = item;
    }

    /**
     * Constructor of a {@link HyriItem}
     *
     * @param plugin The plugin instance
     * @param name The name of the item
     * @param display The display of the item
     * @param description The description of the item
     * @param material The material of the bukkit item
     */
    public HyriItem(T plugin, String name, Supplier<HyriLanguageMessage> display, Supplier<HyriLanguageMessage> description, Material material) {
        this(plugin, name, display, description, new ItemStack(material));
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
     * Get the name of the item
     *
     * @return A name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the display of the item
     *
     * @return A supplier of {@link HyriLanguageMessage}
     */
    public Supplier<HyriLanguageMessage> getDisplay() {
        return this.display;
    }

    /**
     * Get the description of the item
     *
     * @return A supplier of {@link HyriLanguageMessage}
     */
    public Supplier<HyriLanguageMessage> getDescription() {
        return this.description;
    }

    /**
     * Get the bukkit item
     *
     * @return A, {@link ItemStack}
     */
    public ItemStack getItem() {
        return this.item;
    }

}
