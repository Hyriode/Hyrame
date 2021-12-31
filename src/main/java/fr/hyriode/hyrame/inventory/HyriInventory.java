package fr.hyriode.hyrame.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public abstract class HyriInventory implements InventoryHolder {

    /** If its <code>true</code>, when a player clicks, it will be cancelled */
    protected boolean cancelClickEvent = true;

    /** The Spigot inventory */
    protected final Inventory inventory;
    /** The owner of the inventory */
    protected final Player owner;
    /** The display name of the inventory */
    protected final String name;
    /** The size of the inventory */
    protected final int size;
    /** The list of all consumers used when a slot is clicked */
    private final Map<Integer, Consumer<InventoryClickEvent>> clickConsumers;

    /**
     * Constructor of {@link HyriInventory}
     *
     * @param owner The owner of the inventory
     * @param name The name of the inventory
     * @param size The size of the inventory
     */
    public HyriInventory(Player owner, String name, int size) {
        this.owner = owner;
        this.name = name;
        this.size = size;
        this.inventory = Bukkit.createInventory(this, this.size, this.name);
        this.clickConsumers = new HashMap<>();
    }

    /**
     * Get the perfect size for a provided number
     *
     * @param i Number of items, or wanted slots
     * @return A size
     */
    public static int dynamicSize(int i) {
        int size = 0;

        while (size <= i) {
            size++;
        }

        while (size % 9 != 0) {
            size++;
        }

        return size;
    }

    /**
     * Constructor of {@link HyriInventory}
     *
     * @param owner The owner of the inventory
     * @param size The size of the inventory
     */
    public HyriInventory(Player owner, int size) {
        this(owner, "", size);
    }

    /**
     * Constructor of {@link HyriInventory}
     *
     * @param owner The owner of the inventory
     * @param name The name of the inventory
     * @param inventoryType The type of the inventory
     */
    public HyriInventory(Player owner, String name, InventoryType inventoryType) {
        this(owner, name, inventoryType.getDefaultSize());
    }

    /**
     * Constructor of {@link HyriInventory}
     *
     * @param owner The owner of the inventory
     * @param inventoryType The type of the inventory
     */
    public HyriInventory(Player owner, InventoryType inventoryType) {
        this(owner, "", inventoryType.getDefaultSize());
    }

    /**
     * Set an {@link ItemStack} in a given slot
     *
     * @param slot The slot used to the item
     * @param itemStack The {@link ItemStack} to set
     */
    public void setItem(int slot, ItemStack itemStack) {
        this.clickConsumers.remove(slot);
        this.inventory.setItem(slot, itemStack);
    }

    /**
     * Set an {@link ItemStack} in a given slot
     *
     * @param slot The slot used to the item
     * @param itemStack The {@link ItemStack} to set
     * @param clickConsumer The consumer to accept when the slot is clicked
     */
    public void setItem(int slot, ItemStack itemStack, Consumer<InventoryClickEvent> clickConsumer) {
        this.setItem(slot, itemStack);
        if (clickConsumer != null) this.clickConsumers.put(slot, clickConsumer);
    }

    /**
     * Add an {@link ItemStack} in the inventory
     *
     * @param itemStack {@link ItemStack} to add
     */
    public void addItem(ItemStack itemStack) {
        this.setItem(this.inventory.firstEmpty(), itemStack);
    }

    /**
     * Add an {@link ItemStack} in the inventory
     *
     * @param itemStack {@link ItemStack} to add
     * @param clickConsumer The consumer to accept when the slot is clicked
     */
    public void addItem(ItemStack itemStack, Consumer<InventoryClickEvent> clickConsumer) {
        final int slot = this.inventory.firstEmpty();
        this.setItem(slot, itemStack);
        if (clickConsumer != null) this.clickConsumers.put(slot, clickConsumer);
    }

    /**
     * Set a vertical line of an {@link ItemStack}
     *
     * @param startSlot The slot to start
     * @param endSlot The slot to send
     * @param itemStack The {@link ItemStack} to set
     * @param clickConsumer The consumer to accept when the slot is clicked
     */
    public void setVerticalLine(int startSlot, int endSlot, ItemStack itemStack, Consumer<InventoryClickEvent> clickConsumer) {
        for (int i = startSlot; i <= endSlot; i += 9) {
            this.setItem(i, itemStack, clickConsumer);
        }
    }

    /**
     * Set a vertical line of an {@link ItemStack}
     *
     * @param startSlot The slot to start
     * @param endSlot The slot to send
     * @param itemStack The {@link ItemStack} to set
     */
    public void setVerticalLine(int startSlot, int endSlot, ItemStack itemStack) {
        this.setVerticalLine(startSlot, endSlot, itemStack, null);
    }

    /**
     * Set a horizontal line of an {@link ItemStack}
     *
     * @param startSlot The slot to start
     * @param endSlot The slot to send
     * @param itemStack The {@link ItemStack} to set
     * @param clickConsumer The consumer to accept when the slot is clicked
     */
    public void setHorizontalLine(int startSlot, int endSlot, ItemStack itemStack, Consumer<InventoryClickEvent> clickConsumer) {
        for (int i = startSlot; i <= endSlot; i++) {
            this.setItem(i, itemStack, clickConsumer);
        }
    }

    /**
     * Set a horizontal line of an {@link ItemStack}
     *
     * @param startSlot The slot to start
     * @param endSlot The slot to send
     * @param itemStack The {@link ItemStack} to set
     */
    public void setHorizontalLine(int startSlot, int endSlot, ItemStack itemStack) {
        this.setHorizontalLine(startSlot, endSlot, itemStack, null);
    }

    /**
     * Set the {@link ItemStack} that will fill the inventory
     *
     * @param itemStack {@link ItemStack} to use
     */
    public void setFill(ItemStack itemStack) {
        for (int i = 0; i < this.inventory.getSize(); i++) {
            if (this.inventory.getItem(i) == null) {
                this.setItem(i, itemStack);
            }
        }
    }

    /**
     * Open the inventory to the owner
     */
    public void open() {
        this.owner.openInventory(this.inventory);
    }

    /**
     * Update the inventory.<br>
     * Override this method to add an update action
     */
    public void update() {}

    /**
     * Fired when the inventory is opened
     *
     * @param event The triggered event
     */
    public void onOpen(InventoryOpenEvent event){}

    /**
     * Fired when the inventory is closed
     *
     * @param event The triggered event
     */
    public void onClose(InventoryCloseEvent event){}

    /**
     * Fired when a slot in the inventory is clicked
     *
     * @param event The triggered event
     */
    public void onClick(InventoryClickEvent event){}

    /**
     * Get the Spigot inventory object
     *
     * @return {@link Inventory} object
     */
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Get the owner of the inventory
     *
     * @return A {@link Player}
     */
    public Player getOwner() {
        return this.owner;
    }

    /**
     * Get the name of the inventory
     *
     * @return A name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the size of the inventory
     *
     * @return An inventory size
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Get all consumers related to a slot
     *
     * @return A map of {@link Consumer}
     */
    public Map<Integer, Consumer<InventoryClickEvent>> getClickConsumers() {
        return this.clickConsumers;
    }

    /**
     * Check if the click event is cancelled
     *
     * @return <code>true</code> if yes
     */
    public boolean isCancelClickEvent() {
        return this.cancelClickEvent;
    }

    /**
     * Set if the click event is cancelled
     *
     * @param cancelClickEvent New value
     */
    public void setCancelClickEvent(boolean cancelClickEvent) {
        this.cancelClickEvent = cancelClickEvent;
    }


}
