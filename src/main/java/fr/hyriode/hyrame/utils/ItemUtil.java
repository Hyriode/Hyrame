package fr.hyriode.hyrame.utils;

import org.bukkit.ChatColor;
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

    public static boolean removeItemInPlayerInventory(ItemStack initialStack, Player player, long value, String chatHeader) {
        final Inventory inventory = player.getInventory();

        if (value >= 1) {
            if (inventory.containsAtLeast(initialStack, (int) value)) {
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
                return true;
            } else {
                int itemInInventory = 0;

                for (int slot = 0; slot < inventory.getSize(); slot++) {
                    final ItemStack itemStack = inventory.getItem(slot);

                    if (itemStack != null && itemStack.isSimilar(initialStack)) {
                        itemInInventory += itemStack.getAmount();
                    }
                }

                player.sendMessage(chatHeader + ChatColor.RED + (value - itemInInventory) + " " + initialStack.getItemMeta().getDisplayName() + ChatColor.RED +" manquant !");
            }
        } else {
            player.sendMessage(chatHeader + ChatColor.RED + "Merci d'entrer un nombre valide : '" + value + "'.");
        }
        return false;
    }

    public static boolean hasEnoughSlots(Player player, long value) {
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

    public static boolean addItemInPlayerInventory(ItemStack itemStack, Player player, long value) {
        final Inventory inventory = player.getInventory();

        if (hasEnoughSlots(player, itemStack.getAmount())) {
            for (int i = 0; i < value; i++) {
                inventory.addItem(itemStack);
            }
            return true;
        }
        return false;
    }

    public static void dropItems(ItemStack itemStack, long value, Location location) {
        for (int i = 0; i < value; i++) {
            location.getWorld().dropItem(location, itemStack);
        }
    }

    public static boolean isTool(ItemStack itemStack) {
        return isSword(itemStack) || isPickaxe(itemStack) || isAxe(itemStack) || isShovel(itemStack) || isHoe(itemStack);
    }

    public static boolean isSword(ItemStack itemStack) {
        return itemStack.getType().equals(Material.WOOD_SWORD) || itemStack.getType().equals(Material.STONE_SWORD) || itemStack.getType().equals(Material.GOLD_SWORD) || itemStack.getType().equals(Material.IRON_SWORD) || itemStack.getType().equals(Material.GOLD_SWORD) || itemStack.getType().equals(Material.DIAMOND_SWORD);
    }

    public static boolean isPickaxe(ItemStack itemStack) {
        return itemStack.getType().equals(Material.WOOD_PICKAXE) || itemStack.getType().equals(Material.STONE_PICKAXE) || itemStack.getType().equals(Material.GOLD_PICKAXE) || itemStack.getType().equals(Material.IRON_PICKAXE) || itemStack.getType().equals(Material.GOLD_PICKAXE)  || itemStack.getType().equals(Material.DIAMOND_PICKAXE);
    }

    public static boolean isAxe(ItemStack itemStack) {
        return itemStack.getType().equals(Material.WOOD_AXE) || itemStack.getType().equals(Material.STONE_AXE) || itemStack.getType().equals(Material.GOLD_AXE) || itemStack.getType().equals(Material.IRON_AXE) || itemStack.getType().equals(Material.GOLD_AXE) || itemStack.getType().equals(Material.DIAMOND_AXE);
    }

    public static boolean isShovel(ItemStack itemStack) {
        return itemStack.getType().equals(Material.WOOD_SPADE) || itemStack.getType().equals(Material.STONE_SPADE) || itemStack.getType().equals(Material.GOLD_SPADE) || itemStack.getType().equals(Material.IRON_SPADE) || itemStack.getType().equals(Material.GOLD_SPADE) || itemStack.getType().equals(Material.DIAMOND_SPADE);
    }

    public static boolean isHoe(ItemStack itemStack) {
        return itemStack.getType().equals(Material.WOOD_HOE) || itemStack.getType().equals(Material.STONE_HOE) || itemStack.getType().equals(Material.GOLD_HOE) || itemStack.getType().equals(Material.IRON_HOE) || itemStack.getType().equals(Material.GOLD_HOE) || itemStack.getType().equals(Material.DIAMOND_HOE);
    }

    public static boolean isArmor(ItemStack itemStack) {
        return isHelmet(itemStack) || isChestplate(itemStack) || isLeggings(itemStack) || isBoots(itemStack);
    }

    public static boolean isHelmet(ItemStack itemStack) {
        return itemStack.getType().equals(Material.LEATHER_HELMET) || itemStack.getType().equals(Material.CHAINMAIL_HELMET) || itemStack.getType().equals(Material.GOLD_HELMET) || itemStack.getType().equals(Material.IRON_HELMET) || itemStack.getType().equals(Material.GOLD_HELMET) || itemStack.getType().equals(Material.DIAMOND_HELMET);
    }

    public static boolean isChestplate(ItemStack itemStack) {
        return itemStack.getType().equals(Material.LEATHER_CHESTPLATE) || itemStack.getType().equals(Material.CHAINMAIL_CHESTPLATE) || itemStack.getType().equals(Material.GOLD_CHESTPLATE) || itemStack.getType().equals(Material.IRON_CHESTPLATE) || itemStack.getType().equals(Material.GOLD_CHESTPLATE) || itemStack.getType().equals(Material.DIAMOND_CHESTPLATE);
    }

    public static boolean isLeggings(ItemStack itemStack) {
        return itemStack.getType().equals(Material.LEATHER_LEGGINGS) || itemStack.getType().equals(Material.CHAINMAIL_LEGGINGS) || itemStack.getType().equals(Material.GOLD_LEGGINGS) || itemStack.getType().equals(Material.IRON_LEGGINGS) || itemStack.getType().equals(Material.GOLD_LEGGINGS) || itemStack.getType().equals(Material.DIAMOND_LEGGINGS);
    }

    public static boolean isBoots(ItemStack itemStack) {
        return itemStack.getType().equals(Material.LEATHER_BOOTS) || itemStack.getType().equals(Material.CHAINMAIL_BOOTS) || itemStack.getType().equals(Material.GOLD_BOOTS) || itemStack.getType().equals(Material.IRON_BOOTS) || itemStack.getType().equals(Material.GOLD_BOOTS) || itemStack.getType().equals(Material.DIAMOND_BOOTS);
    }

}
