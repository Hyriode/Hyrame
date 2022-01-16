package fr.hyriode.hyrame.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class LocationUtil {

    /**
     * Check if a location is in an area
     *
     * @param location Location to check
     * @param location1 First area location
     * @param location2 Second area location
     * @return <code>true</code> if yes
     */
    public static boolean isInArea(Location location, Location location1, Location location2){
        return new Area(location1, location2).isInArea(location);
    }

    /**
     * Round a given location
     *
     * @param location Location to round
     * @param comma Numbers of digits after the comma
     * @return The new {@link Location}
     */
    public static Location roundLocation(Location location, int comma) {
        return new Location(location.getWorld(), round(location.getX(), comma), round(location.getY(), comma), round(location.getZ(), comma));
    }

    private static double round(double value, int decimals) {
        final double p = Math.pow(10, decimals);

        return Math.round(value * p) / p;
    }

    /**
     * Get the location with the lowest height
     *
     * @param locations The given locations
     * @return The result
     */
    public static Location getLocationWithLowestHeight(Location... locations) {
        Location result = null;
        for (Location location : locations) {
            if (result == null) {
                result = location;
            } else {
                if (location.getY() < result.getY()) {
                    result = location;
                }
            }
        }
        return result;
    }

    /**
     * Drop an item as many times as wanted
     *
     * @param location The location where the {@link ItemStack} will be dropped
     * @param itemStack The {@link ItemStack} to drop
     * @return The dropped item
     */
    public static Item dropItem(Location location, ItemStack itemStack) {
        return location.getWorld().dropItem(location, itemStack);
    }

    /**
     * Check if there is a blocks between two given locations
     *
     * @param first The first {@link Location}
     * @param end The end {@link Location}
     * @return <code>true</code> if there is a block
     */
    public static boolean hasBlockBetween(Location first, Location end) {
        Vector vector = end.toVector().subtract(first.toVector());

        final double j = Math.floor(vector.length());

        vector.multiply(1 / vector.length());

        for (int i = 0; i <= j; i++) {
            vector = end.toVector().subtract(first.toVector());
            vector.multiply(1 / vector.length());

            final Block block = first.getWorld().getBlockAt((first.toVector().add(vector.multiply(i))).toLocation(first.getWorld()));

            if (block.getType().isSolid()) {
                return true;
            }
        }
        return false;
    }

}
