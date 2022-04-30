package fr.hyriode.hyrame.utils;

import fr.hyriode.hyrame.IHyrame;
import org.bukkit.Location;

import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 20/04/2022 at 17:47
 */
public class LocationWrapper {

    private UUID worldId;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public LocationWrapper(UUID worldId, double x, double y, double z, float yaw, float pitch) {
        this.worldId = worldId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public LocationWrapper(UUID worldId, double x, double y, double z) {
        this(worldId, x, y, z, 0.0F, 0.0F);
    }

    public LocationWrapper(Location location) {
        this(location.getWorld().getUID(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public UUID getWorldId() {
        return IHyrame.WORLD.get().getUID();
    }

    public void setWorldId(UUID worldId) {
        this.worldId = worldId;
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
        return new Location(IHyrame.WORLD.get(), this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public static LocationWrapper from(Location location) {
        return new LocationWrapper(location);
    }

}
