package fr.hyriode.hyrame.item;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 14/11/2021 at 09:04
 */
public class HyriItem<T extends JavaPlugin> {

    /** Spigot plugin */
    protected final T plugin;

    /** Item name/id. Example: my_item */
    protected final String name;

    /** Item's display name */
    protected HyriLanguageMessage displayName;

    /** Item's description */
    protected List<String> description;

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
    public HyriItem(T plugin, String name, HyriLanguageMessage displayName, List<String> description, Material material, byte data) {
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
    public HyriItem(T plugin, String name, HyriLanguageMessage displayName, List<String> description, Material material) {
        this(plugin, name, displayName, description, material, (byte) 0);
    }

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
    public HyriItem(T plugin, String name, String displayName, List<String> description, Material material, byte data) {
        this(plugin, name, new HyriLanguageMessage("").addValue(HyriLanguage.EN, displayName), description, material, data);
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
    public HyriItem(T plugin, String name, String displayName, List<String> description, Material material) {
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
    public HyriItem(T plugin, String name, HyriLanguageMessage displayName, Material material, byte data) {
        this(plugin, name, displayName, new ArrayList<>(), material, data);
    }

    /**
     * Constructor of {@link HyriItem}
     *
     * @param plugin Spigot plugin
     * @param name Item's name
     * @param displayName Item's display name
     * @param material Item's material
     */
    public HyriItem(T plugin, String name, HyriLanguageMessage displayName, Material material) {
        this(plugin, name, displayName, new ArrayList<>(), material, (byte) 0);
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
    public HyriItem(T plugin, String name, String displayName, Material material, byte data) {
        this(plugin, name, new HyriLanguageMessage("").addValue(HyriLanguage.EN, displayName), material, data);
    }

    /**
     * Constructor of {@link HyriItem}
     *
     * @param plugin Spigot plugin
     * @param name Item's name
     * @param displayName Item's display name
     * @param material Item's material
     */
    public HyriItem(T plugin, String name, String displayName, Material material) {
        this(plugin, name, new HyriLanguageMessage("").addValue(HyriLanguage.EN, displayName), material, (byte) 0);
    }

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
     * Called when this item is given to a player
     *
     * @param hyrame {@link IHyrame} instance
     * @param player Player with the item
     * @param slot Slot
     * @param itemStack {@link ItemStack}
     */
    public void onGive(IHyrame hyrame, Player player, int slot, ItemStack itemStack) {}

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
    public HyriLanguageMessage getDisplayName() {
        return this.displayName;
    }

    /**
     * Get item's description
     *
     * @return - A list of description lines
     */
    public List<String> getDescription() {
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
