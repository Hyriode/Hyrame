package fr.hyriode.hyrame.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Project: PvpBox
 * Created by AstFaster
 * on 11/07/2021 at 17:01
 */
public class ItemUtil {

    /**
     * The result of the removing action
     */
    public static abstract class RemoveResult {

        /**
         * The remove was successfully done
         */
        public static class Success extends RemoveResult {}

        /**
         * The given amount is invalid (negative)
         */
        public static class InvalidNumber extends RemoveResult {}

        /**
         * The player doesn't have enough resources in his inventory
         */
        public static class MissingResources extends RemoveResult {

            /** The amount of missing resources */
            private final int value;

            /**
             * Constructor of {@link MissingResources}
             *
             * @param value An amount
             */
            public MissingResources(int value) {
                this.value = value;
            }

            /**
             * Get the amount of missing resources in the player inventory
             *
             * @return A number
             */
            public int get() {
                return this.value;
            }

        }

    }

    /**
     * Remove an item from a player inventory
     *
     * @param initialStack The {@link ItemStack} to remove
     * @param player The concerned {@link Player}
     * @param value The amount of the item to remove
     * @return A {@link RemoveResult}
     */
    public static RemoveResult removeItemFromPlayerInventory(ItemStack initialStack, Player player, int value) {
        final Inventory inventory = player.getInventory();

        if (value >= 1) {
            if (inventory.containsAtLeast(initialStack, value)) {
                long count = value;

                for (int slot = 0; slot < inventory.getSize(); slot++) {
                    if (count <= 0) {
                        break;
                    }

                    final ItemStack itemStack = inventory.getItem(slot);

                    if (itemStack != null && itemStack.isSimilar(initialStack)) {
                        final int amount = itemStack.getAmount();

                        if (amount < count) {
                            inventory.setItem(slot, null);
                        } else {
                            itemStack.setAmount((int) (amount - count));
                            inventory.setItem(slot, itemStack);
                        }

                        count -= amount;
                    }
                }
                return new RemoveResult.Success();
            } else {
                int itemsInInventory = 0;

                for (int slot = 0; slot < inventory.getSize(); slot++) {
                    final ItemStack itemStack = inventory.getItem(slot);

                    if (itemStack != null && itemStack.isSimilar(initialStack)) {
                        itemsInInventory += itemStack.getAmount();
                    }
                }
                return new RemoveResult.MissingResources(value - itemsInInventory);
            }
        }
        return new RemoveResult.InvalidNumber();
    }

    /**
     * Check if a player has enough slots to store an amount of items
     *
     * @param player The concerned {@link Player}
     * @param value The amount of items
     * @return <code>true</code> if the player has enough slots, else it will be <code>false</code>
     */
    public static boolean hasEnoughSlots(Player player, int value) {
        final PlayerInventory inventory = player.getInventory();
        final int slotsNeeded = (int) Math.ceil((double) value / 64);

        int count = 0;
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null) {
                count++;
            }
        }

        if (slotsNeeded == 0) {
            return false;
        }

        return count >= slotsNeeded;
    }

    /**
     * Add a given item to a player inventory
     *
     * @param itemStack The {@link ItemStack} to add
     * @param player The concerned {@link Player}
     * @param value The amount of items to add
     * @return <code>true</code> if the items have been given, else it will be <code>false</code> because the player doesn't have enough space
     */
    public static boolean addItemInPlayerInventory(ItemStack itemStack, Player player, int value) {
        final Inventory inventory = player.getInventory();

        if (hasEnoughSlots(player, itemStack.getAmount())) {
            for (int i = 0; i < value; i++) {
                inventory.addItem(itemStack);
            }
            return true;
        }
        return false;
    }

    /**
     * Drop items at a location
     *
     * @param itemStack The {@link ItemStack} to drop
     * @param value The amount of items to drop
     * @param location The location to drop items
     */
    public static void dropItems(ItemStack itemStack, int value, Location location) {
        for (int i = 0; i < value; i++) {
            location.getWorld().dropItem(location, itemStack);
        }
    }

    /**
     * Check if a given item is a tool
     *
     * @param itemStack The {@link ItemStack} to check
     * @return <code>true</code> if yes
     */
    public static boolean isTool(ItemStack itemStack) {
        return isSword(itemStack) || isPickaxe(itemStack) || isAxe(itemStack) || isShovel(itemStack) || isHoe(itemStack);
    }

    /**
     * Check if a given item is a sword
     *
     * @param itemStack The {@link ItemStack} to check
     * @return <code>true</code> if yes
     */
    public static boolean isSword(ItemStack itemStack) {
        return itemStack.getType().equals(Material.WOOD_SWORD) || itemStack.getType().equals(Material.STONE_SWORD) || itemStack.getType().equals(Material.GOLD_SWORD) || itemStack.getType().equals(Material.IRON_SWORD) || itemStack.getType().equals(Material.GOLD_SWORD) || itemStack.getType().equals(Material.DIAMOND_SWORD);
    }

    /**
     * Check if a given item is a pickage
     *
     * @param itemStack The {@link ItemStack} to check
     * @return <code>true</code> if yes
     */
    public static boolean isPickaxe(ItemStack itemStack) {
        return itemStack.getType().equals(Material.WOOD_PICKAXE) || itemStack.getType().equals(Material.STONE_PICKAXE) || itemStack.getType().equals(Material.GOLD_PICKAXE) || itemStack.getType().equals(Material.IRON_PICKAXE) || itemStack.getType().equals(Material.GOLD_PICKAXE)  || itemStack.getType().equals(Material.DIAMOND_PICKAXE);
    }

    /**
     * Check if a given item is an axe
     *
     * @param itemStack The {@link ItemStack} to check
     * @return <code>true</code> if yes
     */
    public static boolean isAxe(ItemStack itemStack) {
        return itemStack.getType().equals(Material.WOOD_AXE) || itemStack.getType().equals(Material.STONE_AXE) || itemStack.getType().equals(Material.GOLD_AXE) || itemStack.getType().equals(Material.IRON_AXE) || itemStack.getType().equals(Material.GOLD_AXE) || itemStack.getType().equals(Material.DIAMOND_AXE);
    }

    /**
     * Check if a given item is a shovel
     *
     * @param itemStack The {@link ItemStack} to check
     * @return <code>true</code> if yes
     */
    public static boolean isShovel(ItemStack itemStack) {
        return itemStack.getType().equals(Material.WOOD_SPADE) || itemStack.getType().equals(Material.STONE_SPADE) || itemStack.getType().equals(Material.GOLD_SPADE) || itemStack.getType().equals(Material.IRON_SPADE) || itemStack.getType().equals(Material.GOLD_SPADE) || itemStack.getType().equals(Material.DIAMOND_SPADE);
    }

    /**
     * Check if a given item is a hoe
     *
     * @param itemStack The {@link ItemStack} to check
     * @return <code>true</code> if yes
     */
    public static boolean isHoe(ItemStack itemStack) {
        return itemStack.getType().equals(Material.WOOD_HOE) || itemStack.getType().equals(Material.STONE_HOE) || itemStack.getType().equals(Material.GOLD_HOE) || itemStack.getType().equals(Material.IRON_HOE) || itemStack.getType().equals(Material.GOLD_HOE) || itemStack.getType().equals(Material.DIAMOND_HOE);
    }

    /**
     * Check if a given item is an armor
     *
     * @param itemStack The {@link ItemStack} to check
     * @return <code>true</code> if yes
     */
    public static boolean isArmor(ItemStack itemStack) {
        return isHelmet(itemStack) || isChestplate(itemStack) || isLeggings(itemStack) || isBoots(itemStack);
    }

    /**
     * Check if a given item is a helmet
     *
     * @param itemStack The {@link ItemStack} to check
     * @return <code>true</code> if yes
     */
    public static boolean isHelmet(ItemStack itemStack) {
        return itemStack.getType().equals(Material.LEATHER_HELMET) || itemStack.getType().equals(Material.CHAINMAIL_HELMET) || itemStack.getType().equals(Material.GOLD_HELMET) || itemStack.getType().equals(Material.IRON_HELMET) || itemStack.getType().equals(Material.GOLD_HELMET) || itemStack.getType().equals(Material.DIAMOND_HELMET);
    }

    /**
     * Check if a given item is a chestplate
     *
     * @param itemStack The {@link ItemStack} to check
     * @return <code>true</code> if yes
     */
    public static boolean isChestplate(ItemStack itemStack) {
        return itemStack.getType().equals(Material.LEATHER_CHESTPLATE) || itemStack.getType().equals(Material.CHAINMAIL_CHESTPLATE) || itemStack.getType().equals(Material.GOLD_CHESTPLATE) || itemStack.getType().equals(Material.IRON_CHESTPLATE) || itemStack.getType().equals(Material.GOLD_CHESTPLATE) || itemStack.getType().equals(Material.DIAMOND_CHESTPLATE);
    }

    /**
     * Check if a given item is a leggings
     *
     * @param itemStack The {@link ItemStack} to check
     * @return <code>true</code> if yes
     */
    public static boolean isLeggings(ItemStack itemStack) {
        return itemStack.getType().equals(Material.LEATHER_LEGGINGS) || itemStack.getType().equals(Material.CHAINMAIL_LEGGINGS) || itemStack.getType().equals(Material.GOLD_LEGGINGS) || itemStack.getType().equals(Material.IRON_LEGGINGS) || itemStack.getType().equals(Material.GOLD_LEGGINGS) || itemStack.getType().equals(Material.DIAMOND_LEGGINGS);
    }

    /**
     * Check if a given item is a boots
     *
     * @param itemStack The {@link ItemStack} to check
     * @return <code>true</code> if yes
     */
    public static boolean isBoots(ItemStack itemStack) {
        return itemStack.getType().equals(Material.LEATHER_BOOTS) || itemStack.getType().equals(Material.CHAINMAIL_BOOTS) || itemStack.getType().equals(Material.GOLD_BOOTS) || itemStack.getType().equals(Material.IRON_BOOTS) || itemStack.getType().equals(Material.GOLD_BOOTS) || itemStack.getType().equals(Material.DIAMOND_BOOTS);
    }

}
