package fr.hyriode.hyrame.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class SerializerUtil {

    public static String serializeComponent(BaseComponent[] components) {
        return ComponentSerializer.toString(components);
    }

    /**
     * Transform a player inventory to an array of string
     *
     * @param playerInventory {@link PlayerInventory} to transform
     * @return An array of string
     */
    public static String[] playerInventoryToStringArray(PlayerInventory playerInventory){
        final String content = inventoryToString(playerInventory);
        final String armor = itemStackArrayToString(playerInventory.getArmorContents());

        return new String[] {content, armor};
    }

    /**
     * Transform an array of string to a player inventory
     *
     * @param inventory {@link PlayerInventory} to change
     * @param strings Array of string
     */
    public static void stringArrayToPlayerInventory(PlayerInventory inventory, String[] strings) {
        final ItemStack[] content = Objects.requireNonNull(stringToInventory(strings[0])).getContents();
        final ItemStack[] armor = itemStackArrayFromString(strings[1]);

        inventory.setContents(content);
        inventory.setArmorContents(armor);
    }

    /**
     * Transform an inventory to a string
     *
     * @param inventory Inventory to serialize
     * @return Serialized inventory
     */
    public static String inventoryToString(Inventory inventory) {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final BukkitObjectOutputStream data = new BukkitObjectOutputStream(outputStream);

            data.writeInt(inventory.getSize());
            data.writeUTF(inventory.getName());

            for (int i = 0; i < inventory.getSize(); i++) {
                data.writeObject(inventory.getItem(i));
            }

            data.close();

            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Transform a string to an inventory
     *
     * @param string Given input
     * @return {@link Inventory}
     */
    public static Inventory stringToInventory(String string) {
        try {
            final byte[] bytes = Base64.getDecoder().decode(string);
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            final BukkitObjectInputStream data = new BukkitObjectInputStream(inputStream);
            final Inventory inventory = Bukkit.createInventory(null, data.readInt(), data.readUTF());

            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) data.readObject());
            }

            data.close();

            return inventory;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Transform an {@link ItemStack} to a string
     *
     * @param itemStack {@link ItemStack} to serialize
     * @return Serialized inventory
     */
    public static String itemStackToString(ItemStack itemStack) {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final BukkitObjectOutputStream data = new BukkitObjectOutputStream(outputStream);

            data.writeObject(itemStack);
            data.close();

            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Transform a string to an {@link ItemStack}
     *
     * @param string Given input
     * @return An {@link ItemStack}
     */
    public static ItemStack stringToItemStack(String string) {
        try {
            final byte[] bytes = Base64.getDecoder().decode(string);
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            final BukkitObjectInputStream data = new BukkitObjectInputStream(inputStream);
            final ItemStack itemStack = (ItemStack) data.readObject();

            data.close();

            return itemStack;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Transform an array of items to a string
     *
     * @param items {@link ItemStack}
     * @return A string
     */
    public static String itemStackArrayToString(ItemStack[] items) {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.length);

            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Transform a string to an array of item
     *
     * @param data Given input
     * @return An array of {@link ItemStack}
     */
    public static ItemStack[] itemStackArrayFromString(String data) {
        try {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            final BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            final ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();

            return items;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
