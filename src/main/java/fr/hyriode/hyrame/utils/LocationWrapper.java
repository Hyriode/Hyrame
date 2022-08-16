package fr.hyriode.hyrame.utils;

import com.google.gson.annotations.Expose;
import fr.hyriode.hyrame.IHyrame;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 20/04/2022 at 17:47
 */
public class LocationWrapper {

    @Expose(serialize = false, deserialize = false)
    private Location location;

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public LocationWrapper(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public LocationWrapper(double x, double y, double z) {
        this(x, y, z, 0.0F, 0.0F);
    }

    public LocationWrapper(Location location) {
        this(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public Location asBukkit() {
        return this.asBukkit(IHyrame.WORLD.get());
    }

    public Location asBukkit(World world) {
        return this.location == null || !this.location.getWorld().getName().equals(world.getName()) ? this.location = new Location(world, this.x, this.y, this.z, this.yaw, this.pitch) : this.location;
    }

    public static LocationWrapper from(Location location) {
        return new LocationWrapper(location);
    }

}
