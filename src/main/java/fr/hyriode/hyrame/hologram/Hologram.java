package fr.hyriode.hyrame.hologram;

import fr.hyriode.hyrame.packet.PacketUtil;
import fr.hyriode.hyrame.utils.ListUtil;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 08/01/2022 at 11:12
 */
public class Hologram {

    /** The set with all holograms */
    private static final Set<Hologram> HOLOGRAMS = new HashSet<>();

    /** The maximum range a player can see the hologram */
    private static final double RANGE_VIEW = 32.0D;

    private final BukkitTask linesTask;

    /** All the players that receive the hologram */
    private final Map<Player, Boolean> receivers;
    /** All the entities that players see */
    private final Map<Player, Map<Integer, EntityArmorStand>> entities;

    /** The Spigot plugin instance */
    private final JavaPlugin plugin;
    /** The hologram location */
    private Location location;

    /** The distance between each line of the hologram */
    private final double linesDistance;
    /** All the lines used for the hologram */
    private final Map<Integer, Line> lines;

    /** The interaction callback ran when the hologram is right-clicked by a player */
    private final Consumer<Player> interactionCallback;

    /**
     * Constructor of {@link  Hologram}
     *
     * @param plugin The Spigot plugin
     * @param location The hologram location
     * @param linesDistance The distance between each line
     * @param lines The lines used for the hologram
     * @param interactionCallback The interaction callback
     */
    protected Hologram(JavaPlugin plugin, Location location, double linesDistance, Map<Integer, Line> lines, Consumer<Player> interactionCallback) {
        this.plugin = plugin;
        this.location = location;
        this.linesDistance = linesDistance;
        this.lines = lines;
        this.interactionCallback = interactionCallback;
        this.receivers = new HashMap<>();
        this.entities = new HashMap<>();

        this.startUpdates();

        this.linesTask = Bukkit.getScheduler().runTaskTimer(plugin, this::sendLines, 10L, 10L);

        HOLOGRAMS.add(this);
    }

    /**
     * Destroy the hologram
     */
    public void destroy() {
        this.linesTask.cancel();

        for (Line line : this.lines.values()) {
            line.cancelUpdate();
        }

        this.removeLines();

        this.lines.clear();
        this.entities.clear();
        this.receivers.clear();

        HOLOGRAMS.remove(this);
    }

    /**
     * Add a receiver.<br>
     * This new receiver will receive the hologram.
     *
     * @param player The player to add as a receiver
     */
    public void addReceiver(Player player) {
        final boolean range = this.isInRange(player);

        this.generateEntities(player);

        if (range) {
            this.sendLines0(player);
        }

        this.receivers.put(player, range);
    }

    /**
     * Remove a given player from the receivers.<br>
     * The player will no longer see the hologram.
     *
     * @param player The player to remove from the receivers
     */
    public void removeReceiver(Player player) {
        this.receivers.remove(player);

        this.removeLines(player);
    }

    /**
     * Send all lines to the receivers
     */
    public void sendLines() {
        for (Player player : this.receivers.keySet()) {
            this.sendLines(player);
        }
    }

    /**
     * Send all lines to a given player
     *
     * @param player The player who will receive the lines
     */
    public void sendLines(Player player) {
        if (this.receivers.containsKey(player)) {
            final boolean wasRange = this.receivers.get(player);
            final boolean range = this.isInRange(player);

            if (wasRange == range) {
                return;
            } else if (wasRange) {
                this.removeLines(player);
            } else {
                this.sendLines0(player);
            }

            this.receivers.put(player, range);
        }
    }

    /**
     * The action of sending lines
     *
     * @param player The player
     */
    private void sendLines0(Player player) {
        final Map<Integer, EntityArmorStand> entities = this.entities.get(player);

        if (entities != null) {
            for (Map.Entry<Integer, EntityArmorStand> entry : entities.entrySet()) {
                this.sendLine(player, entry.getKey());
            }
        }
    }

    /**
     * Send a given line to the player
     *
     * @param player The concerned player
     * @param line The number of the line to send
     */
    public void sendLine(Player player, int line) {
        final Map<Integer, EntityArmorStand> entities = this.entities.get(player);

        if (entities != null) {
            final EntityArmorStand armorStand = entities.get(line);

            if (armorStand != null) {
                this.sendEntity(player, armorStand);
                this.sendMetadata(player, armorStand);
            }
        }
    }

    /**
     * Set the value of a given line
     *
     * @param index The index of the line
     * @param line The new line
     */
    public void setLine(int index, Line line) {
        this.lines.put(index, line);
        this.updateLine(index);
    }

    /**
     * Remove all lines from players
     */
    public void removeLines() {
        for (Player player : this.receivers.keySet()) {
            this.removeLines(player);
        }
    }

    /**
     * Remove all lines from a player
     *
     * @param player The player
     */
    public void removeLines(Player player) {
        final Map<Integer, EntityArmorStand> entities = this.entities.get(player);

        if (entities == null) {
            return;
        }

        for (Map.Entry<Integer, EntityArmorStand> entry : entities.entrySet()) {
            this.removeLine(player, entry.getKey());
        }
    }

    /**
     * Remove a given line from a player
     *
     * @param player The concerned
     * @param line The number of the line
     */
    public void removeLine(Player player, int line) {
        final Map<Integer, EntityArmorStand> entities = this.entities.get(player);

        if (entities == null) {
            return;
        }

        this.removeEntity(player, entities.get(line));
    }

    /**
     * Update a given line from all receivers
     *
     * @param slot The number of the line
     */
    public void updateLine(int slot) {
        final Line line = this.lines.get(slot);

        if (line != null) {
            for (Player player : this.receivers.keySet()) {
                final Map<Integer, EntityArmorStand> entities = this.entities.get(player);

                if (entities == null) {
                    continue;
                }

                final EntityArmorStand entity = entities.get(slot);

                entity.setCustomName(line.getValue(player));

                this.sendMetadata(player, entity);
            }
        }
    }

    /**
     * Update all the existing lines of the hologram
     */
    public void updateLines() {
        for (int slot : this.lines.keySet()) {
            this.updateLine(slot);
        }
    }

    /**
     * Start the updates of all lines
     */
    private void startUpdates() {
        for (Map.Entry<Integer, Line> entry : this.lines.entrySet()) {
            final Line line = entry.getValue();
            final Line.Update update = line.getUpdate();

            if (update != null) {
                final Supplier<BukkitTask> task = () -> new BukkitRunnable() {
                    @Override
                    public void run() {
                        update.getAction().accept(line);

                        updateLine(entry.getKey());
                    }
                }.runTaskTimerAsynchronously(this.plugin, 0L, update.getTicks());

                update.setTask(task.get());
            }
        }
    }

    /**
     * Generate entities for a player.<br>
     * It will create all the {@link EntityArmorStand} with the name provided by the line.
     *
     * @param player The concerned player
     */
    private void generateEntities(Player player) {
        final Map<Integer, EntityArmorStand> entities = this.entities.containsKey(player) ? this.entities.get(player) : new HashMap<>();
        final Location first = this.location.clone().add(0, this.lines.size() * (this.linesDistance + 0.05D), 0);

        for (int i = 0; i <= ListUtil.getMaxValue(new ArrayList<>(this.lines.keySet())); i++) {
            final Line line = this.lines.get(i);

            if (line != null) {
                final String value = line.getValue(player);

                if (value != null && !value.isEmpty()) {
                    entities.put(i, this.createEntity(first.clone(), value));
                }
            }

            first.subtract(0, this.linesDistance + 0.05D, 0);
        }

        this.entities.put(player, entities);
    }

    /**
     * This method is used to create an {@link EntityArmorStand} by given its location and its display name
     *
     * @param location The entity {@link Location}
     * @param text The text to used as the display name of the entity
     * @return A {@link EntityArmorStand} object
     */
    private EntityArmorStand createEntity(Location location, String text) {
        final EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle());

        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCustomName(text);

        if (this.interactionCallback == null) {
            armorStand.n(true);
        }

        armorStand.setBasePlate(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);

        return armorStand;
    }

    /**
     * Send an entity to a player's client
     *
     * @param player The concerned player
     * @param armorStand The entity to send
     */
    private void sendEntity(Player player, EntityArmorStand armorStand) {
        if (armorStand != null) {
            PacketUtil.sendPacket(player, new PacketPlayOutSpawnEntity(armorStand, 78));
        }
    }

    /**
     * Send an entity to all receivers' clients
     *
     * @param armorStand The entity to send
     */
    private void sendEntity(EntityArmorStand armorStand) {
        for (Player player : this.receivers.keySet()) {
            this.sendEntity(player, armorStand);
        }
    }

    /**
     * Remove an entity from a player's client
     *
     * @param player The concerned player
     * @param armorStand The entity to remove
     */
    private void removeEntity(Player player, EntityArmorStand armorStand) {
        if (armorStand != null) {
            PacketUtil.sendPacket(player, new PacketPlayOutEntityDestroy(armorStand.getId()));
        }
    }

    /**
     * Remove an entity from all receivers' clients
     *
     * @param armorStand The entity to remove
     */
    private void removeEntity(EntityArmorStand armorStand) {
        for (Player player : this.receivers.keySet()) {
            this.removeEntity(player, armorStand);
        }
    }

    /**
     * Send an entity's metadata to a player's client
     *
     * @param player The concerned player
     * @param armorStand The entity with the metadata to send
     */
    private void sendMetadata(Player player, EntityArmorStand armorStand) {
        if (armorStand != null) {
            PacketUtil.sendPacket(player, new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), true));
        }
    }

    /**
     * Send an entity's metadata to all receivers' clients
     *
     * @param armorStand The entity with the metadata to send
     */
    private void sendMetadata(EntityArmorStand armorStand) {
        for (Player player : this.receivers.keySet()) {
            this.sendMetadata(player, armorStand);
        }
    }

    /**
     * Check if a player is in the range of the hologram
     *
     * @param player The concerned player
     * @return <code>true</code> if the player is in the range
     */
    private boolean isInRange(Player player) {
        return player.getLocation().getWorld() == this.location.getWorld() && player.getLocation().distance(this.location) <= RANGE_VIEW;
    }

    /**
     * Set the hologram location
     *
     * @param location The {@link Location}
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Get the hologram location
     *
     * @return A {@link Location} object
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * Get all players that receive the hologram
     *
     * @return A set of {@link Player}
     */
    public Set<Player> getReceivers() {
        return this.receivers.keySet();
    }

    /**
     * Get all lines used for the hologram
     *
     * @return A map of lines
     */
    public Map<Integer, Line> getLines() {
        return this.lines;
    }

    /**
     * Get the distance between each line
     *
     * @return The lines' distance
     */
    public double getLinesDistance() {
        return this.linesDistance;
    }

    /**
     * Get the interaction callback to trigger when a player right click
     *
     * @return A consumer of {@link Player}
     */
    public Consumer<Player> getInteractionCallback() {
        return this.interactionCallback;
    }

    /**
     * Check if the hologram contains a given entity
     *
     * @param entityId The identifier of the entity
     * @return <code>true</code> if it contains the entity
     */
    public boolean containsEntity(int entityId) {
        for (Map<Integer, EntityArmorStand> map : this.entities.values()) {
            for (Map.Entry<Integer, EntityArmorStand> entry : map.entrySet()) {
                if (entry.getValue().getId() == entityId) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if the hologram is distance from a given location in a custom range
     *
     * @param location The location
     * @param range The maximum range
     * @return <code>true</code> if the location is correctly distanced
     */
    public boolean isDistancedFrom(Location location, double range) {
        for (Map<Integer, EntityArmorStand> map : this.entities.values()) {
            for (Map.Entry<Integer, EntityArmorStand> entry : map.entrySet()) {
                final EntityArmorStand entity = entry.getValue();

                if (new Location(entity.world.getWorld(), entity.locX, entity.locY, entity.locZ).distance(location) <= range) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get all the created holograms
     *
     * @return A set of {@link Hologram}
     */
    public static Set<Hologram> getAll() {
        return HOLOGRAMS;
    }

    /**
     * The class that represents a line for the hologram
     */
    public static class Line {

        /** The position of the line in the hologram */
        private int position;

        /** The value of the line. The function is applied each time a player need the value. */
        private Function<Player, String> value;
        /** The line update */
        private Update update;

        /**
         * Constructor of {@link Line}
         *
         * @param value The value of the line as a {@link Supplier}
         */
        public Line(Supplier<String> value) {
            this.withValueFromString(value);
        }

        /**
         * Constructor of {@link Line}
         *
         * @param value The value of the line as a {@link Function}
         */
        public Line(Function<Player, String> value) {
            this.value = value;
        }

        String getValue(Player player) {
            return this.value.apply(player);
        }

        public Line withValue(Function<Player, String> value) {
            this.value = value;
            return this;
        }

        public Line withValueFromString(Supplier<String> value) {
            return this.withValue(target -> value.get());
        }

        public Update getUpdate() {
            return this.update;
        }

        public Line withUpdate(int ticks, Consumer<Line> action) {
            this.update = new Update(ticks, action);
            return this;
        }

        public void cancelUpdate() {
            if (this.update != null) {
                this.update.getTask().cancel();
                this.update = null;
            }
        }

        public int getPosition() {
            return this.position;
        }

        void setPosition(int position) {
            this.position = position;
        }

        static Line from(String value) {
            return new Line(() -> value);
        }

        public static class Update {

            private BukkitTask task;

            private final int ticks;
            private final Consumer<Line> action;

            public Update(int ticks, Consumer<Line> action) {
                this.ticks = ticks;
                this.action = action;
            }

            public BukkitTask getTask() {
                return this.task;
            }

            void setTask(BukkitTask task) {
                this.task = task;
            }

            public int getTicks() {
                return this.ticks;
            }

            public Consumer<Line> getAction() {
                return this.action;
            }

        }

    }

    public static class Builder {

        private JavaPlugin plugin;

        private Location location;

        private double linesDistance = 0.30D;
        private Map<Integer, Line> lines = new HashMap<>();

        private Consumer<Player> interactionCallback;

        public Builder(JavaPlugin plugin) {
            this.plugin = plugin;
        }

        public Builder(JavaPlugin plugin, Location location) {
            this.plugin = plugin;
            this.location = location;
        }

        public Builder withPlugin(JavaPlugin plugin) {
            this.plugin = plugin;
            return this;
        }

        public Builder withLocation(Location location) {
            this.location = location;
            return this;
        }

        public Builder withLinesDistance(double linesDistance) {
            this.linesDistance = linesDistance;
            return this;
        }

        public Builder withLines(Map<Integer, Line> lines) {
            this.lines = lines;
            return this;
        }

        public Builder withLines(List<Line> lines) {
            this.lines.clear();

            for (int i = 0; i < lines.size(); i++) {
                this.lines.put(i, lines.get(i));
            }
            return this;
        }

        public Builder withLine(int slot, Line line) {
            this.lines.put(slot, line);
            return this;
        }

        public Builder withBlankLine(int slot) {
            return this.withLine(slot, new Line(() -> ""));
        }

        public Builder withLine(Line line) {
            return this.withLine(this.lines.size(), line);
        }

        public Builder withLinesAsString(Map<Integer, String> lines) {
            final Map<Integer, Line> newLines = new HashMap<>();

            for (Map.Entry<Integer, String> entry : lines.entrySet()) {
                newLines.put(entry.getKey(), Line.from(entry.getValue()));
            }

            this.lines = newLines;
            return this;
        }

        public Builder withLinesAsString(List<String> lines) {
            this.lines.clear();

            for (int i = 0; i < lines.size(); i++) {
                this.lines.put(i, Line.from(lines.get(i)));
            }
            return this;
        }

        public Builder withLine(int slot, Supplier<String> line) {
            this.lines.put(slot, new Line(line));
            return this;
        }

        public Builder withLine(String line) {
            return this.withLine(this.lines.size(), Line.from(line));
        }

        public Builder withInteractionCallback(Consumer<Player> interactionCallback) {
            this.interactionCallback = interactionCallback;
            return this;
        }

        public Hologram build() {
            if (this.plugin != null && this.lines != null) {
                for (Map.Entry<Integer, Line> entry : this.lines.entrySet()) {
                    entry.getValue().setPosition(entry.getKey());
                }

                return new Hologram(this.plugin, this.location, this.linesDistance, this.lines, this.interactionCallback);
            }
            throw new RuntimeException("Couldn't set a null value to a hologram builder field!");
        }

    }

}
