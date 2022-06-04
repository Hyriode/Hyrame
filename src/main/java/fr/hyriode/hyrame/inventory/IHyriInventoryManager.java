package fr.hyriode.hyrame.inventory;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/03/2022 at 09:04
 */
public interface IHyriInventoryManager {

    /**
     * Close all inventories with the same class
     *
     * @param inventoryClass The class of the inventories to close
     */
    void closeInventories(Class<? extends HyriInventory> inventoryClass);

    /**
     * Get the current inventory of a player
     *
     * @param player The concerned player
     * @return A {@link HyriInventory} object
     */
    HyriInventory getPlayerInventory(Player player);

    /**
     * Check if the current inventory of the player is a given one
     *
     * @param player The concerned player
     * @param inventoryClass The class of the inventory
     * @return <code>true</code> if the player has the given inventory
     */
    boolean isPlayerInventory(Player player, Class<? extends HyriInventory> inventoryClass);

    /**
     * Get the inventory of each player
     *
     * @return A map of player {@link UUID} and {@link HyriInventory}
     */
    Map<UUID, HyriInventory> getPlayersInventories();

    /**
     * Get all inventories that have the same class. But also with the {@link UUID} of the players that own them
     *
     * @param inventoryClass The {@link Class} of the inventories to look for
     * @param <T> The type of inventories to return
     * @return A map of {@link HyriInventory} and {@link UUID}
     */
    <T extends HyriInventory> Map<T, UUID> getInventoriesMap(Class<T> inventoryClass);


    /**
     * Get all inventories that have the same class
     *
     * @param inventoryClass The {@link Class} of the inventories to look for
     * @param <T> The type of inventories to return
     * @return A list of {@link HyriInventory}
     */
    <T extends HyriInventory> List<T> getInventories(Class<T> inventoryClass);

}
