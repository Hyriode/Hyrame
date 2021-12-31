package fr.hyriode.hyrame.utils;

import org.bukkit.Location;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class LocationUtil {

    /**
     * Check if a location is in an area
     *
     * @param location - Location to check
     * @param location1 - First area location
     * @param location2 - Second area location
     * @return - <code>true</code> if yes
     */
    public static boolean isInArea(Location location, Location location1, Location location2){
        return location.getX() >= Math.min(location1.getX(), location2.getX()) && location.getX() <= Math.max(location1.getX(), location2.getX()) &&
                location.getY() >= Math.min(location1.getY(), location2.getY()) && location.getY() <= Math.max(location1.getY(), location2.getY()) &&
                location.getZ() >= Math.min(location1.getZ(), location2.getZ()) && location.getZ() <= Math.max(location1.getZ(), location2.getZ());
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

}
