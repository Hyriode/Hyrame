package fr.hyriode.hyrame.enchantment;

import fr.hyriode.hyggdrasil.api.util.builder.BuildException;
import fr.hyriode.hyggdrasil.api.util.builder.BuilderEntry;
import fr.hyriode.hyggdrasil.api.util.builder.IBuilder;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.item.ItemNBT;
import fr.hyriode.hyrame.packet.PacketUtil;
import fr.hyriode.hyrame.utils.ItemUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class EnchantmentGUI {

    /** Represents the state of the inventory being open */
    private boolean open;

    /** The inventory that is used on the Bukkit side */
    private Inventory inventory;
    private EnchantContainer container;
    /** The listener holder class */
    private final Handler listener = new Handler();

    /** The {@link Plugin} used */
    private final Plugin plugin;
    /** The {@link Player} concerned by this gui */
    private final Player player;

    /** The state that decides if the {@link Player} is able to close this gui */
    private final boolean preventClose;
    /** A {@link Consumer} called when the gui is closed */
    private final Consumer<Player> closeConsumer;
    private final CloseScenario closeScenario;

    private final int[] costs;
    /** All the slot in the GUI */
    private final List<Slot> slots;

    private final boolean resetEnchantments;
    private final boolean multipleEnchantments;
    private final boolean autoCombustible;

    public EnchantmentGUI(Plugin plugin, Player player, boolean preventClose, Consumer<Player> closeConsumer, CloseScenario closeScenario, int[] costs, Slot[] slots, boolean resetEnchantments, boolean multipleEnchantments, boolean autoCombustible) {
        this.plugin = plugin;
        this.player = player;
        this.preventClose = preventClose;
        this.closeConsumer = closeConsumer;
        this.closeScenario = closeScenario;
        this.costs = costs;
        this.slots = new ArrayList<>(Arrays.asList(slots));
        this.resetEnchantments = resetEnchantments;
        this.multipleEnchantments = multipleEnchantments;
        this.autoCombustible = autoCombustible;
    }

    /**
     * Open the gui to the player
     */
    public void open() {
        this.open = true;

        final EntityPlayer nmsPlayer = ((CraftPlayer) this.player).getHandle();
        this.container = new EnchantContainer(nmsPlayer);

        CraftEventFactory.handleInventoryCloseEvent(nmsPlayer);
        Bukkit.getPluginManager().registerEvents(this.listener, this.plugin);

        this.inventory = this.container.getBukkitView().getTopInventory();

        if (this.autoCombustible) {
            this.slots.add(new Slot(SlotType.RIGHT, new ItemBuilder(Material.INK_SACK, 64, 4).build()));
        }


        for (Slot slot : this.slots) {
            this.inventory.setItem(slot.getType().getValue(), slot.getItemStack());
        }

        // The container id of the inventory, used for NMS methods
        final int containerId = nmsPlayer.nextContainerCounter();

        PacketUtil.sendPacket(player, new PacketPlayOutOpenWindow(containerId, "minecraft:enchanting_table", new ChatMessage(Blocks.ENCHANTING_TABLE.a() + ".name")));

        nmsPlayer.activeContainer = this.container;
        this.container.windowId = containerId;

        this.container.addSlotListener(nmsPlayer);
        this.container.checkReachable = false;
    }

    private void refillCombustible() {
        if (this.autoCombustible) {
            this.inventory.setItem(SlotType.RIGHT.getValue(), new ItemBuilder(Material.INK_SACK, 64, 4).build());
        }
    }

    private Slot getSlot(int slotNumber) {
        for (Slot slot : this.slots) {
            if (slot.getType().getValue() == slotNumber) {
                return slot;
            }
        }
        return null;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public List<Slot> getSlots() {
        return this.slots;
    }

    public boolean isOpen() {
        return this.open;
    }

    public static class Builder implements IBuilder<EnchantmentGUI> {

        private final BuilderEntry<Plugin> pluginEntry = new BuilderEntry<Plugin>("Plugin instance").required();
        private final BuilderEntry<Player> playerEntry = new BuilderEntry<Player>("Player object").required();
        private final BuilderEntry<Boolean> preventCloseEntry = new BuilderEntry<>("Prevent close value", () -> false);
        private final BuilderEntry<Consumer<Player>> closeConsumerEntry = new BuilderEntry<>("Close consumer", () -> null);
        private final BuilderEntry<CloseScenario> closeScenarioEntry = new BuilderEntry<>("Close scenario", () -> CloseScenario.DROP_ITEMS);
        private final BuilderEntry<int[]> costsEntry = new BuilderEntry<>("Enchantment costs", () -> new int[]{1, 5, 8});
        private final BuilderEntry<Slot[]> slotsEntry = new BuilderEntry<>("Inventory slots", () -> new Slot[] {});
        private final BuilderEntry<Boolean> resetEnchantmentsEntry = new BuilderEntry<>("Resetting enchantments value", () -> false);
        private final BuilderEntry<Boolean> multipleEnchantmentsEntry = new BuilderEntry<>("Multiple enchantments value", () -> false);
        private final BuilderEntry<Boolean> autoCombustibleEntry = new BuilderEntry<>("Auto combustible", () -> true);

        public Builder(Plugin plugin, Player player) {
            this.pluginEntry.set(() -> plugin);
            this.playerEntry.set(() -> player);
        }

        public Builder withPreventClose(boolean value) {
            this.preventCloseEntry.set(() -> value);
            return this;
        }

        public Builder withCloseConsumer(Consumer<Player> closeConsumer) {
            this.closeConsumerEntry.set(() -> closeConsumer);
            return this;
        }

        public Builder withCloseScenario(CloseScenario scenario) {
            this.closeScenarioEntry.set(() -> scenario);
            return this;
        }

        public Builder withCosts(int... costs) {
            if (costs.length != 3 && costs.length != 1) {
                throw new IllegalArgumentException("Enchantment costs must be 3!");
            }

            this.costsEntry.set(() -> costs);
            return this;
        }

        public Builder withSlots(Slot... slots) {
            this.slotsEntry.set(() -> slots);
            return this;
        }

        public Builder withResetEnchantments(boolean value) {
            this.resetEnchantmentsEntry.set(() -> value);
            return this;
        }

        public Builder withMultipleEnchantments(boolean value) {
            this.multipleEnchantmentsEntry.set(() -> value);
            return this;
        }

        public Builder withAutoCombustible(boolean value) {
            this.autoCombustibleEntry.set(() -> value);
            return this;
        }

        @Override
        public EnchantmentGUI build() throws BuildException {
            return new EnchantmentGUI(
                    this.pluginEntry.get(), this.playerEntry.get(), this.preventCloseEntry.get(),
                    this.closeConsumerEntry.get(), this.closeScenarioEntry.get(), this.costsEntry.get(),
                    this.slotsEntry.get(), this.resetEnchantmentsEntry.get(), this.multipleEnchantmentsEntry.get(),
                    this.autoCombustibleEntry.get()
            );
        }

    }

    /**
     * Holds the listeners for the GUI
     */
    private class Handler implements Listener {

        private static final String ITEM_TAG = "EnchantmentItem";

        @EventHandler
        public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
            if (!event.getInventory().equals(inventory)) {
                return;
            }

            if (multipleEnchantments) {
                event.setCancelled(false);
            }

            final int[] costsOffered = event.getExpLevelCostsOffered();

            if (costs.length == 3) {
                System.arraycopy(costs, 0, costsOffered, 0, 3);
            } else if (costs.length == 1) {
                final net.minecraft.server.v1_8_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(event.getItem());
                final int maxCost = costs[0];
                final Random random = new Random(container.f);

                costsOffered[0] = EnchantmentManager.a(random, 0, maxCost / 2,itemStack);
                costsOffered[1] = EnchantmentManager.a(random, 1, maxCost / 2, itemStack);
                costsOffered[2] = maxCost;
            }
        }

        @EventHandler
        public void onItemEnchant(EnchantItemEvent event) {
            if (!event.getInventory().equals(inventory)) {
                return;
            }

            refillCombustible();
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            if (!event.getInventory().equals(inventory)) {
                return;
            }

            final Slot slot = getSlot(event.getRawSlot());

            if (slot != null) {
                event.setCancelled(slot.isCancelled());
            }

            // Reset enchantments in table
            if (resetEnchantments && event.getRawSlot() == SlotType.LEFT.getValue() && event.getCurrentItem() != null) {
                final EntityHuman human = ((CraftPlayer) event.getWhoClicked()).getHandle();
                final InventorySubcontainer enchantSlots = container.enchantSlots;

                human.enchantDone(0);

                enchantSlots.update();
                container.f = human.cj();
                container.a(enchantSlots);
            }
        }

        @EventHandler
        public void onItemDrop(ItemSpawnEvent event) {
            final ItemStack itemStack = event.getEntity().getItemStack();

            if (itemStack == null) {
                return;
            }

            if (new ItemNBT(itemStack).hasTag(ITEM_TAG)) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            if (event.getInventory().equals(inventory)) {
                if (preventClose) {
                    open();
                } else {
                    open = false;

                    if (closeScenario == CloseScenario.GIVE_ITEMS) {
                        for (SlotType slotType : SlotType.values()) {
                            final int slotNumber = slotType.getValue();
                            final ItemStack itemStack = inventory.getItem(slotNumber);

                            if (itemStack == null) {
                                continue;
                            }

                            final Slot slot = getSlot(slotNumber);

                            if ((slot != null && !slot.isAcceptingScenarios()) || ItemUtil.addItemInPlayerInventory(itemStack, (Player) event.getPlayer())) {
                                inventory.setItem(slotNumber, null);
                            }
                        }
                    }

                    if (closeConsumer != null) {
                        closeConsumer.accept(player);
                    }
                }
            }
        }
    }

    public enum CloseScenario {
        DROP_ITEMS, GIVE_ITEMS
    }

    public static class Slot {

        private final SlotType type;
        private final ItemStack itemStack;
        private final boolean cancelled;
        private final boolean acceptingScenarios;

        public Slot(SlotType type, ItemStack itemStack, boolean cancelled, boolean acceptingScenarios) {
            this.type = type;
            this.itemStack = itemStack;
            this.cancelled = cancelled;
            this.acceptingScenarios = acceptingScenarios;
        }

        public Slot(SlotType type, ItemStack itemStack) {
            this(type, itemStack, true, false);
        }

        public SlotType getType() {
            return this.type;
        }

        public ItemStack getItemStack() {
            return this.itemStack;
        }

        public boolean isCancelled() {
            return this.cancelled;
        }

        public boolean isAcceptingScenarios() {
            return this.acceptingScenarios;
        }

    }

    public enum SlotType {

        LEFT(0),
        RIGHT(1);

        private final int value;

        SlotType(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

    }

    private static class EnchantContainer extends ContainerEnchantTable  {

        public EnchantContainer(EntityHuman entityhuman) {
            super(entityhuman.inventory, entityhuman.world, new BlockPosition(0, 0, 0));
        }

        @Override
        public boolean c(EntityHuman entityhuman) {
            return true;
        }
    }

}
