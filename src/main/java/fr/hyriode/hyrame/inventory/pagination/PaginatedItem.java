package fr.hyriode.hyrame.inventory.pagination;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 22/05/2022 at 07:43
 *
 * This class represents a paginated item.<br>
 * A paginated item is basically a {@linkplain ItemStack bukkit item} but with an event in optional
 */
public class PaginatedItem {

    /** The supplier of the {@linkplain ItemStack bukkit item} object */
    private final Supplier<ItemStack> bukkitItem;
    /** The click event related to the item; it can be <code>null</code> */
    private final Consumer<InventoryClickEvent> eventConsumer;

    /**
     * Constructor of a {@linkplain PaginatedItem paginated item}
     *
     * @param bukkitItem The bukkit item
     * @param eventConsumer The event related to the item
     */
    public PaginatedItem(Supplier<ItemStack> bukkitItem, Consumer<InventoryClickEvent> eventConsumer) {
        this.bukkitItem = bukkitItem;
        this.eventConsumer = eventConsumer;
    }

    /**
     * Get the paginated item as {@link ItemStack bukkit's type}
     *
     * @return The {@link ItemStack} object
     */
    public ItemStack asBukkit() {
        return this.bukkitItem.get();
    }

    /**
     * Get the event consumer of the item
     *
     * @return A consumer of {@link InventoryClickEvent}
     */
    public Consumer<InventoryClickEvent> getEventConsumer() {
        return this.eventConsumer;
    }

    /**
     * Create a {@linkplain PaginatedItem paginated item} from an {@link ItemStack}
     *
     * @param bukkitItem The {@linkplain ItemStack bukkit item}
     * @return The created {@link PaginatedItem}
     */
    public static PaginatedItem from(ItemStack bukkitItem) {
        return new PaginatedItem(() -> bukkitItem, null);
    }

    /**
     * Create a {@linkplain PaginatedItem paginated item} from an {@link ItemStack} and its related event
     *
     * @param bukkitItem The {@linkplain ItemStack bukkit item}
     * @param eventConsumer The event related to the item
     * @return The created {@link PaginatedItem}
     */
    public static PaginatedItem from(ItemStack bukkitItem, Consumer<InventoryClickEvent> eventConsumer) {
        return new PaginatedItem(() -> bukkitItem, eventConsumer);
    }


    /**
     * Create a {@linkplain PaginatedItem paginated item} from an {@link ItemStack}
     *
     * @param bukkitItem The supplier of the {@linkplain ItemStack bukkit item}
     * @return The created {@link PaginatedItem}
     */
    public static PaginatedItem from(Supplier<ItemStack> bukkitItem) {
        return new PaginatedItem(bukkitItem, null);
    }

    /**
     * Create a {@linkplain PaginatedItem paginated item} from an {@link ItemStack} and its related event
     *
     * @param bukkitItem The supplier of the {@linkplain ItemStack bukkit item}
     * @param eventConsumer The event related to the item
     * @return The created {@link PaginatedItem}
     */
    public static PaginatedItem from(Supplier<ItemStack> bukkitItem, Consumer<InventoryClickEvent> eventConsumer) {
        return new PaginatedItem(bukkitItem, eventConsumer);
    }

}
