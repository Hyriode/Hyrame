package fr.hyriode.tools;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class SerializerUtil {

    /**
     * Serialize an inventory to a string
     *
     * @param inventory - Inventory to serialize
     * @return - Serialized inventory
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
     * Deserialize a string to an inventory
     *
     * @param string - String to deserialize
     * @return - Deserialized inventory
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
     * Serialize an item to a string
     *
     * @param itemStack - Item to serialize
     * @return - Serialized item
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
     * Deserialize a string to an item
     *
     * @param string - String to deserialize
     * @return - Deserialized item
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


}
