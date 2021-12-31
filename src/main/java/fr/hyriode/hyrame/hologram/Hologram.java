package fr.hyriode.hyrame.hologram;

import fr.hyriode.hyrame.utils.PacketUtil;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class Hologram {

    /** Some constants */
    private static final double DISTANCE = 0.24D;
    private static final double RANGE_VIEW = 60.0D;

    /** Lines task */
    private final BukkitTask linesTask;

    /** Entities */
    private final Map<Integer, EntityArmorStand> entities;

    /** Receivers */
    private final Map<Player, Boolean> receivers;

    /** Are lines changed */
    private boolean linesChanged;

    /** Hologram's location */
    private Location location;

    /** Hologram's lines */
    private List<String> lines;

    /**
     * Constructor of {@link Hologram}
     *
     * @param plugin - Spigot plugin
     * @param lines - Lines
     */
    public Hologram(JavaPlugin plugin, String... lines) {
        this.lines = Arrays.asList(lines);
        this.linesChanged = true;
        this.receivers = new HashMap<>();
        this.entities = new HashMap<>();

        this.linesTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::sendLines, 10L, 10L);
    }

    /**
     * Add a receiver
     *
     * @param player - Player to add
     */
    public void addReceiver(Player player) {
        boolean range = false;

        if (player.getLocation().getWorld() == this.location.getWorld() && player.getLocation().distance(this.location) <= RANGE_VIEW) {
            range = true;

            this.sendLines(player);
        }

        this.receivers.put(player, range);
    }

    /**
     * Remove a receiver
     *
     * @param player - Player to remove
     */
    public void removeReceiver(Player player) {
        this.receivers.remove(player);

        this.removeLines(player);
    }

    /**
     * Generate lines at a location
     *
     * @param location - Location
     */
    public void generateLines(Location location) {
        final Location first = location.clone().add(0, ((float) this.lines.size() / 2) * DISTANCE, 0);

        for (int i = 0; i < this.lines.size(); i++) {
            this.entities.put(i, this.generateEntitiesForLine(first.clone(), this.lines.get(i)));

            first.subtract(0, DISTANCE, 0);
        }

        this.location = location;
    }

    /**
     * Generate lines by using default location
     */
    public void generateLines() {
        this.generateLines(this.location);
    }

    /**
     * Send lines to a player
     *
     * @param player - Player
     */
    public void sendLines(Player player) {
        for (int i = 0; i < this.lines.size(); i++) {
            this.sendPacketForLine(player, i);
        }
    }

    /**
     * Send lines to all receivers
     */
    public void sendLines() {
        for (Player player : this.receivers.keySet()) {
            final boolean range = player.getLocation().getWorld() == this.location.getWorld() && player.getLocation().distance(this.location) <= RANGE_VIEW;
            final boolean wasRange = this.receivers.get(player);

            if (this.linesChanged && range) {
                this.sendLines(player);

                this.linesChanged = false;
            } else if (wasRange == range) {
                continue;
            } else if (wasRange) {
                this.removeLines(player);
            } else {
                this.sendLines(player);
            }

            this.receivers.put(player, range);
        }
    }

    /**
     * Remove a line to a player
     *
     * @param player - Player
     * @param line - Line's number
     */
    public void removeLine(Player player, int line) {
        final EntityArmorStand armorStand = this.entities.get(line);

        if (armorStand != null) {
            PacketUtil.sendPacket(player, new PacketPlayOutEntityDestroy(armorStand.getId()));
        }
    }

    /**
     * Remove a line to a player
     *
     * @param player - Player
     */
    public void removeLines(Player player) {
        for (int i = 0; i < this.lines.size(); i++) {
            this.removeLine(player, i);
        }
    }

    /**
     * Remove a line to all receivers
     */
    public void removeLines() {
        for (Player player : this.receivers.keySet()) {
            this.removeLines(player);
        }
    }

    private EntityArmorStand generateEntitiesForLine(Location location, String text) {
        final EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle());

        armorStand.setSize(0.00001F, 0.00001F);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCustomName(text);
        armorStand.setCustomNameVisible(true);
        armorStand.setLocation(location.getX(), location.getY() - 2, location.getZ(), 0, 0);

        return armorStand;
    }

    private void sendPacketForLine(Player player, int line) {
        final EntityArmorStand armorStand = this.entities.get(line);

        if (armorStand != null) {
            PacketUtil.sendPacket(player, new PacketPlayOutSpawnEntity(armorStand, 78));
            PacketUtil.sendPacket(player, new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), true));
        }
    }

    /**
     * Change lines
     *
     * @param lines - New lines
     */
    public void change(String... lines) {
        this.removeLines();

        this.clearEntities();

        this.lines = Arrays.asList(lines);
        this.linesChanged = true;

        this.generateLines();
    }

    /**
     * Destroy hologram
     */
    public void destroy() {
        this.removeLines();
        this.clearEntities();

        this.location = null;
    }

    /**
     * Fully destroy hologram
     */
    public void fullDestroy() {
        this.linesTask.cancel();

        this.destroy();

        this.receivers.clear();
    }

    /**
     * Clear hologram's entities
     */
    public void clearEntities() {
        this.entities.clear();
    }

    /**
     * Set hologram's location
     *
     * @param location - New location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Get hologram's location
     *
     * @return - New location
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * Get hologram lines
     *
     * @return - A list of lines
     */
    public List<String> getLines() {
        return this.lines;
    }

}
