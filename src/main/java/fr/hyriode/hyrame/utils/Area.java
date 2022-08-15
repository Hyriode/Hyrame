package fr.hyriode.hyrame.utils;

import fr.hyriode.hyrame.utils.block.Cuboid;
import org.bukkit.Location;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 15/01/2022 at 08:51
 */
public class Area {

    private Cuboid cuboid;

    /** The minimum location */
    private final Location min;
    /** The maximum location */
    private final Location max;

    /**
     * Constructor of {@link Area}
     *
     * @param first First point of the area
     * @param second Second point of the area
     */
    public Area(Location first, Location second)
    {
        this.min = new Location(null, 0, 0, 0);
        this.max = new Location(null, 0, 0, 0);

        if (first.getWorld() != second.getWorld()) {
            return;
        }

        if (first.getX() >= second.getX()) {
            this.max.setX(first.getX());
            this.min.setX(second.getX());
        } else {
            this.max.setX(second.getX());
            this.min.setX(first.getX());
        }

        if (first.getY() >= second.getY()) {
            this.max.setY(first.getY());
            this.min.setY(second.getY());
        } else {
            this.max.setY(second.getY());
            this.min.setY(first.getY());
        }

        if (first.getZ() >= second.getZ()) {
            this.max.setZ(first.getZ());
            this.min.setZ(second.getZ());
        } else {
            this.max.setZ(second.getZ());
            this.min.setZ(first.getZ());
        }

        this.min.setWorld(first.getWorld());
        this.max.setWorld(first.getWorld());
    }

    /**
     * Get area size in X axis
     *
     * @return A size
     */
    public int getSizeX() {
        return this.max.getBlockX() - this.min.getBlockX();
    }

    /**
     * Get area size in Y axis
     *
     * @return A size
     */
    public int getSizeY() {
        return this.max.getBlockY() - this.min.getBlockY();
    }

    /**
     * Get area size in Z axis
     *
     * @return A size
     */
    public int getSizeZ() {
        return this.max.getBlockZ() - this.min.getBlockZ();
    }

    /**
     * Get the lower point
     *
     * @return A {@link Location}
     */
    public Location getMin() {
        return this.min;
    }

    /**
     * Get the higher point
     *
     * @return A {@link Location}
     */
    public Location getMax() {
        return this.max;
    }

    /**
     * Check if a given location is in the area
     *
     * @param location Location
     *
     * @return <code>true</code> if the location is in the area
     */
    public boolean isInArea(Location location) {
        return location.getWorld() == this.min.getWorld() && location.getWorld() == this.max.getWorld() &&
                location.getX() >= Math.min(this.min.getX(), this.max.getX()) && location.getX() <= Math.max(this.min.getX(), this.max.getX()) &&
                location.getY() >= Math.min(this.min.getY(), this.max.getY()) && location.getY() <= Math.max(this.min.getY(), this.max.getY()) &&
                location.getZ() >= Math.min(this.min.getZ(), this.max.getZ()) && location.getZ() <= Math.max(this.min.getZ(), this.max.getZ());
    }

    /**
     * Check if a given location is in a range around the area
     *
     * @param location The {@link  Location}
     * @param range The range radius
     *
     * @return <code>true</code> if the {@link Location} is in the range
     */
    public boolean isInRange(Location location, int range) {
        if (location == null) {
            return false;
        } else if (location.getX() > this.max.getX() + range || this.min.getX() - range > location.getX()) {
            return false;
        } else if (location.getY() > this.max.getY() + range || this.min.getY() - range > location.getY()) {
            return false;
        } else {
            return !(location.getZ() > this.max.getZ() + range) && !(this.min.getZ() - range > location.getZ());
        }
    }

    public Cuboid toCuboid() {
        return this.cuboid == null ? this.cuboid = new Cuboid(this.max, this.min) : this.cuboid;
    }

}
