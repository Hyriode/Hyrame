package fr.hyriode.hyrame.inventory;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

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

    /** The update of the inventory */
    protected Update update;

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
     * Get the correct inventory name from a language message
     *
     * @param player The player
     * @param key The key of the message
     * @return The inventory name
     */
    protected static String name(Player player, String key) {
        return HyriLanguageMessage.get(key).getValue(player);
    }

    /**
     * Get the perfect size for a provided number
     *
     * @param i Number of items, or wanted slots
     * @return A size
     */
    protected static int dynamicSize(int i) {
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

        if (clickConsumer != null) {
            this.clickConsumers.put(slot, clickConsumer);
        }
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

        if (clickConsumer != null) {
            this.clickConsumers.put(slot, clickConsumer);
        }
    }

    /**
     * Apply a pattern on the inventory
     *
     * @param pattern The {@linkplain IPattern pattern} to apply
     * @param itemStack The {@linkplain ItemStack item} used by the pattern
     * @param eventConsumer The event triggered when a player clicks it
     * @param slots The slots asked by the pattern
     */
    public void applyPattern(IPattern pattern, ItemStack itemStack, Consumer<InventoryClickEvent> eventConsumer, int... slots) {
        pattern.apply(this, itemStack, eventConsumer, slots);
    }

    /**
     * Apply a pattern on the inventory
     *
     * @param pattern The {@linkplain IPattern pattern} to apply
     * @param itemStack The {@linkplain ItemStack item} used by the pattern
     * @param slots The slots asked by the pattern
     */
    public void applyPattern(IPattern pattern, ItemStack itemStack, int... slots) {
        pattern.apply(this, itemStack, slots);
    }

    /**
     * Apply a design on the inventory
     *
     * @param design The {@linkplain IDesign design} to apply
     * @param data The data of the design
     * @param <T> The type of the data
     */
    public <T> void applyDesign(IDesign<T> design, T data) {
        design.apply(this, data);
    }

    /**
     * Apply a design on the inventory
     *
     * @param design The {@linkplain IDesign design} to apply
     * @param <T> The type of the data
     */
    public <T> void applyDesign(IDesign<T> design) {
        design.apply(this);
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
        this.applyPattern(Pattern.VERTICAL, itemStack, clickConsumer, startSlot, endSlot);
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
        this.applyPattern(Pattern.HORIZONTAL, itemStack, clickConsumer, startSlot, endSlot);
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
    public void onOpen(InventoryOpenEvent event) {}

    /**
     * Fired when the inventory is closed
     *
     * @param event The triggered event
     */
    public void onClose(InventoryCloseEvent event) {}

    /**
     * Fired when a slot in the inventory is clicked
     *
     * @param event The triggered event
     */
    public void onClick(InventoryClickEvent event) {}

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
     * Get the update of the inventory
     *
     * @return The inventory's {@linkplain Update update}
     */
    public Update getUpdate() {
        return this.update;
    }

    /**
     * Set the new update of the inventory
     *
     * @param ticks The ticks to wait between each update
     */
    public void newUpdate(long ticks) {
        if (this.update != null) {
            this.update.cancel();
        }

        this.update = new Update(this, ticks);
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

    /**
     * The update class used to add auto-update
     */
    public static class Update {

        private boolean started;

        private BukkitTask task;

        private final HyriInventory inventory;
        private final long ticks;

        public Update(HyriInventory inventory, long ticks) {
            this.inventory = inventory;
            this.ticks = ticks;
        }

        public void start(JavaPlugin plugin) {
            if (this.started) {
                return;
            }

            this.started = true;
            this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this.inventory::update, this.ticks, this.ticks);
        }

        public void cancel() {
            if (this.started) {
                this.task.cancel();
                this.task = null;
                this.started = false;
            }
        }

    }

    /**
     * A pattern represents a way to display groups of items easily on a {@link HyriInventory}.<br>
     * For example: a pattern to display items vertically/horizontally, etc.
     */
    @FunctionalInterface
    public interface IPattern {

        void apply(HyriInventory inventory, ItemStack itemStack, Consumer<InventoryClickEvent> eventConsumer, int... slots);

        default void apply(HyriInventory inventory, ItemStack itemStack, int... slots) {
            this.apply(inventory, itemStack, null, slots);
        }

    }

    /**
     * Some default implementation of {@link IPattern}
     */
    public static class Pattern {

        /** Pattern used to display items horizontally */
        public static final IPattern HORIZONTAL = (inventory, itemStack, eventConsumer, slots) -> {
            if (slots.length != 2) {
                throw new IllegalArgumentException("Invalid slots number!");
            }

            for (int i = slots[0]; i <= slots[1]; i++) {
                inventory.setItem(i, itemStack, eventConsumer);
            }
        };

        /** Pattern used to display items vertically */
        public static final IPattern VERTICAL = (inventory, itemStack, eventConsumer, slots) -> {
            if (slots.length != 2) {
                throw new IllegalArgumentException("Invalid slots number!");
            }

            for (int i = slots[0]; i <= slots[1]; i += 9) {
                inventory.setItem(i, itemStack, eventConsumer);
            }
        };

        /** Pattern used to fill an area of a {@link HyriInventory} with a given item */
        public static final IPattern FILL = (inventory, itemStack, eventConsumer, slots) -> {
            if (slots.length != 2) {
                throw new IllegalArgumentException("Invalid slots number!");
            }

            final int start = slots[0];
            final int end = slots[1];

            for (int y = start / 9; y <= end / 9; y++) {
                for (int x = start % 9; x <= (end % 9 == 0 ? 8 : end % 9); x++) {
                    final int slot = y * 9 + x;

                    inventory.setItem(slot, itemStack, eventConsumer);
                }
            }
        };

    }

    /**
     * A design represents a way to display repetitive designs inside multiples {@link HyriInventory}.
     * It can be helpful to avoid duplicated code used to design {@link HyriInventory}.
     *
     * @param <T> The data used by the design
     */
    @FunctionalInterface
    public interface IDesign<T> {

        /**
         * Apply the design on a given {@linkplain HyriInventory inventory}
         *
         * @param inventory The inventory to apply the design
         * @param data The data needed by the design
         */
        void apply(HyriInventory inventory, T data);

        /**
         * Default method to apply the design on a given {@linkplain HyriInventory inventory} but without any data.
         *
         * @param inventory The inventory to apply the design
         */
        default void apply(HyriInventory inventory) {
            this.apply(inventory, null);
        }

    }

    /**
     * Default implementations of {@link IDesign}
     */
    public static class Design {

        /** A border design. It sets a border of {@link Material#STAINED_GLASS_PANE} around the {@link HyriInventory}. */
        public static final IDesign<Byte> BORDER = new IDesign<Byte>() {

            private final int[] sixLines = new int[]{0, 1, 2, 6, 7, 8, 9, 17, 36, 44, 45, 46, 47, 51, 52, 53};
            private final int[] fiveLines = new int[]{0, 1, 2, 6, 7, 8, 36, 37, 38, 42, 43, 44};
            private final int[] fourLines = new int[]{0, 1, 2, 6, 7, 8, 27, 28, 29, 33, 34, 35};

            @Override
            public void apply(HyriInventory inventory, Byte color) {
                if (color == null) {
                    color = (byte) 9;
                }

                int[] slots = new int[0];
                switch (inventory.getSize()) {
                    case 6 * 9:
                        slots = sixLines;
                        break;
                    case 5 * 9:
                        slots = fiveLines;
                        break;
                    case 4 * 9:
                        slots = fourLines;
                        break;
                }

                if (slots.length == 0) {
                    return;
                }

                final ItemStack item = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, color)
                        .withName(" ")
                        .withAllItemFlags()
                        .build();

                for (int slot : slots) {
                    inventory.setItem(slot, item);
                }
            }
        };

    }

}
