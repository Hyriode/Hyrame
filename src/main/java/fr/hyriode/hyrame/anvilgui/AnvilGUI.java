package fr.hyriode.hyrame.anvilgui;

import fr.hyriode.hyrame.utils.PacketUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class AnvilGUI {

    /** The {@link Plugin} used */
    private final Plugin plugin;
    /** The {@link Player} concerned by this gui */
    private final Player player;

    /** The gui {@link ItemStack} input in left slot */
    private ItemStack inputLeft;
    /** The gui {@link ItemStack} input in right slot */
    private ItemStack inputRight;

    /** The state that decides if the {@link Player} is able to close this gui */
    private final boolean preventClose;
    /** Represents the state of the inventory being open */
    private boolean open;

    /** A {@link Consumer} called when the gui is closed */
    private final Consumer<Player> closeConsumer;
    /** A {@link Consumer} called when the {@link Slot#INPUT_LEFT} slot is clicked */
    private final Consumer<Player> inputLeftClickConsumer;
    /** A {@link Consumer} called when the {@link Slot#INPUT_RIGHT} slot is clicked */
    private final Consumer<Player> inputRightClickConsumer;
    /** An {@link BiFunction} called when the {@link Slot#OUTPUT} slot is clicked */
    private final BiFunction<Player, String, Response> completeFunction;

    /** The container id of the inventory, used for NMS methods */
    private int containerId;
    /** The inventory that is used on the Bukkit side */
    private Inventory inventory;
    /** The listener holder class */
    private final Handler listener = new Handler();

    public AnvilGUI(Plugin plugin, Player player,
                    ItemStack inputLeft, ItemStack inputRight, boolean preventClose,
                    Consumer<Player> closeConsumer, Consumer<Player> inputLeftClickConsumer,
                    Consumer<Player> inputRightClickConsumer,
                    BiFunction<Player, String, Response> completeFunction) {
        this.plugin = plugin;
        this.player = player;
        this.inputLeft = inputLeft;
        this.inputRight = inputRight;
        this.preventClose = preventClose;
        this.closeConsumer = closeConsumer;
        this.inputLeftClickConsumer = inputLeftClickConsumer;
        this.inputRightClickConsumer = inputRightClickConsumer;
        this.completeFunction = completeFunction;
    }

    public AnvilGUI(Plugin plugin, Player player, String itemText,
                    ItemStack inputRight, boolean preventClose,
                    Consumer<Player> closeConsumer, Consumer<Player> inputLeftClickConsumer,
                    Consumer<Player> inputRightClickConsumer,
                    BiFunction<Player, String, Response> completeFunction) {
        this(plugin, player, defaultInput(itemText), inputRight, preventClose, closeConsumer, inputLeftClickConsumer, inputRightClickConsumer, completeFunction);
    }

    /**
     * Set a default input item
     *
     * @param itemText Text to display on the item
     * @return An {@link ItemStack}
     */
    private static ItemStack defaultInput(String itemText) {
        final ItemStack input = new ItemStack(Material.PAPER);
        if (itemText != null) {
            final ItemMeta itemMeta = input.getItemMeta();

            itemMeta.setDisplayName(itemText);

            input.setItemMeta(itemMeta);
        }
        return input;
    }

    /**
     * Open the gui to the player
     */
    public void open() {
        final EntityPlayer nmsPlayer = ((CraftPlayer) this.player).getHandle();

        CraftEventFactory.handleInventoryCloseEvent(nmsPlayer);

        nmsPlayer.activeContainer = nmsPlayer.defaultContainer;

        Bukkit.getPluginManager().registerEvents(this.listener, this.plugin);

        final Container container = new AnvilContainer(nmsPlayer);

        this.inventory = container.getBukkitView().getTopInventory();

        if (this.inputLeft != null) {
            this.inventory.setItem(Slot.INPUT_LEFT.get(), this.inputLeft);
        }
        if (this.inputRight != null) {
            this.inventory.setItem(Slot.INPUT_RIGHT.get(), this.inputRight);
        }

        this.containerId = nmsPlayer.nextContainerCounter();

        PacketUtil.sendPacket(player, new PacketPlayOutOpenWindow(containerId, "minecraft:anvil", new ChatMessage(Blocks.ANVIL.a() + ".name")));

        nmsPlayer.activeContainer = container;
        container.windowId = containerId;

        container.addSlotListener(nmsPlayer);

        this.open = true;
    }

    /**
     * Close the gui
     *
     * @param withClosePacket Send the packet or not
     */
    public void close(boolean withClosePacket) {
        if (this.open) {
            this.open = false;

            HandlerList.unregisterAll(this.listener);

            if (withClosePacket) {
                final EntityPlayer nmsPlayer = ((CraftPlayer) this.player).getHandle();

                CraftEventFactory.handleInventoryCloseEvent(nmsPlayer);

                nmsPlayer.activeContainer = nmsPlayer.defaultContainer;

                PacketUtil.sendPacket(this.player, new PacketPlayOutCloseWindow(this.containerId));
            }

            if (this.closeConsumer != null) {
                this.closeConsumer.accept(this.player);
            }
        }
    }

    public void close() {
        this.close(true);
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getInputLeft() {
        return this.inputLeft;
    }

    public void setInputLeft(ItemStack inputLeft) {
        this.inputLeft = inputLeft;
    }

    public ItemStack getInputRight() {
        return this.inputRight;
    }

    public void setInputRight(ItemStack inputRight) {
        this.inputRight = inputRight;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Holds the listeners for the GUI
     */
    private class Handler implements Listener {

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            final Player clicker = (Player) event.getWhoClicked();
            if (event.getInventory().equals(inventory) && (event.getRawSlot() < 3 || event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))) {

                event.setCancelled(true);

                if (event.getRawSlot() == Slot.OUTPUT.get()) {
                    final ItemStack clicked = inventory.getItem(Slot.OUTPUT.get());
                    if (clicked == null || clicked.getType() == Material.AIR) return;

                    final Response response = completeFunction.apply(clicker, clicked.hasItemMeta() ? clicked.getItemMeta().getDisplayName() : "");

                    if (response.getText() != null) {
                        final ItemMeta meta = clicked.getItemMeta();

                        meta.setDisplayName(response.getText());

                        clicked.setItemMeta(meta);

                        inventory.setItem(Slot.INPUT_LEFT.get(), clicked);
                    } else if (response.getInventoryToOpen() != null) {
                        clicker.openInventory(response.getInventoryToOpen());
                    } else {
                        close();
                    }
                } else if (event.getRawSlot() == Slot.INPUT_LEFT.get()) {
                    if (inputLeftClickConsumer != null) {
                        inputLeftClickConsumer.accept(player);
                    }
                } else if (event.getRawSlot() == Slot.INPUT_RIGHT.get()) {
                    if (inputRightClickConsumer != null) {
                        inputRightClickConsumer.accept(player);
                    }
                }
            }
        }

        @EventHandler
        public void onInventoryDrag(InventoryDragEvent event) {
            if (event.getInventory().equals(inventory)) {
                for (int slot : Slot.getAll()) {
                    if (event.getRawSlots().contains(slot)) {
                        event.setCancelled(true);
                        break;
                    }
                }
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            if (open && event.getInventory().equals(inventory)) {
                close(false);
                if (preventClose) {
                    Bukkit.getScheduler().runTask(plugin, AnvilGUI.this::open);
                }
            }
        }
    }

    /**
     * Represents a response when the player clicks the output item in the AnvilGUI
     */
    public static class Response {

        /** The text that has to be displayed to the user */
        private final String text;
        private final Inventory openInventory;

        /**
         * Creates a response to the user's input
         *
         * @param text The text that is to be displayed to the user, which can be null to close the inventory
         */
        private Response(String text, Inventory openInventory) {
            this.text = text;
            this.openInventory = openInventory;
        }

        /**
         * Gets the text that is to be displayed to the user
         *
         * @return The text that is to be displayed to the user
         */
        public String getText() {
            return this.text;
        }

        /**
         * Get the inventory that should be opened
         *
         * @return The inventory that should be opened
         */
        public Inventory getInventoryToOpen() {
            return this.openInventory;
        }

        /**
         * Returns a {@link Response} for when the AnvilGUI need to be closed
         *
         * @return A {@link Response}
         */
        public static Response close() {
            return new Response(null, null);
        }

        /**
         * Returns a {@link Response} for when AnvilGUI is to display text to the user
         *
         * @param text The text to be displayed to the user
         * @return A {@link Response}
         */
        public static Response text(String text) {
            return new Response(text, null);
        }

        /**
         * Returns a {@link Response} for when the GUI should open the provided inventory
         *
         * @param inventory The inventory to open
         * @return The {@link Response} to return
         */
        public static Response openInventory(Inventory inventory) {
            return new Response(null, inventory);
        }

    }


    /**
     * Class wrapping the magic constants of slot numbers in an AnvilGUI
     */
    public enum Slot {

        /**
         * The slot on the left, where the first input is inserted. An {@link ItemStack} is always inserted
         * here to be renamed
         */
        INPUT_LEFT(0),

        /**
         * Not used, but in a real anvil you are able to put the second item you want to combine here
         */
        INPUT_RIGHT(1),

        /**
         * The output slot, where an item is put when two items are combined from {@link Slot#INPUT_LEFT} and
         * {@link Slot#INPUT_RIGHT} or when {{@link Slot#INPUT_LEFT} item is renamed
         */
        OUTPUT(2);

        private final int value;

        Slot(int value) {
            this.value = value;
        }

        public int get() {
            return this.value;
        }

        public static int[] getAll() {
            return new int[] {INPUT_LEFT.get(), INPUT_RIGHT.get(), OUTPUT.get()};
        }

    }

    /**
     * Modifications to {@link ContainerAnvil} that makes you don't need to have xp to use this anvil
     */
    private static class AnvilContainer extends ContainerAnvil {

        public AnvilContainer(EntityHuman entityhuman) {
            super(entityhuman.inventory, entityhuman.world, new BlockPosition(0, 0, 0), entityhuman);
        }

        @Override
        public boolean a(EntityHuman human) {
            return true;
        }

        @Override
        public void b(EntityHuman entityhuman) {}

    }

}
