package fr.hyriode.hyrame.npc;

import com.mojang.authlib.GameProfile;
import fr.hyriode.hyrame.hologram.Hologram;
import fr.hyriode.hyrame.reflection.entity.EnumItemSlot;
import fr.hyriode.hyrame.utils.PacketUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class NPC extends EntityPlayer {

    /** Interaction callback */
    protected NPCInteractCallback interactCallback;

    /** NPC equipment */
    protected final Map<EnumItemSlot, ItemStack> equipment;

    /** NPC Hologram */
    protected Hologram hologram;

    /** Players */
    protected Set<Player> players;

    /** Is NPC showing to all players */
    protected  boolean showingToAll;

    /** Is NPCs head tracking players */
    protected boolean trackingPlayer;

    /** NPCs location */
    protected Location location;

    /* Spigot plugin* */
    protected final JavaPlugin plugin;

    /**
     * Constructor of {@link NPC}
     *
     * @param plugin - Spigot plugin
     * @param location - NPCs location
     * @param world - NPCs world
     * @param gameProfile - NPCs profile
     */
    public NPC(JavaPlugin plugin, Location location, World world, GameProfile gameProfile) {
        super(world.getServer().getServer(), (WorldServer) world, gameProfile, new PlayerInteractManager(world));
        this.plugin = plugin;
        this.trackingPlayer = false;
        this.showingToAll = true;
        this.players = new HashSet<>();
        this.equipment = new HashMap<>();

        this.setLocation(location);
    }

    /**
     * Get NPCs location
     *
     * @return - A {@link Location} object
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * Set NPCs location
     *
     * @param location - New location
     * @return - This NPC object (useful for inline pattern)
     */
    public NPC setLocation(Location location) {
        this.location = location;

        this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        for (Player player : Bukkit.getOnlinePlayers()) {
            PacketUtil.sendPacket(player, this.getTeleportPacket());
        }

        return this;
    }

    /**
     * Check if NPCs is tracking players with its head
     *
     * @return - <code>true</code> if yes
     */
    public boolean isTrackingPlayer() {
        return this.trackingPlayer;
    }

    /**
     * Set if NPC is tracking players with its head
     *
     * @param trackingPlayer - New value
     * @return - This NPC object (useful for inline pattern)
     */
    public NPC setTrackingPlayer(boolean trackingPlayer) {
        this.trackingPlayer = trackingPlayer;

        return this;
    }

    /**
     * Check if NPC is showing to all players
     *
     * @return - <code>true</code> if yes
     */
    public boolean isShowingToAll() {
        return this.showingToAll;
    }

    /**
     * Set if NPC is showing to all players
     *
     * @param showingToAll - New value
     * @return - This NPC object (useful for inline pattern)
     */
    public NPC setShowingToAll(boolean showingToAll) {
        this.showingToAll = showingToAll;

        return this;
    }

    /**
     * Get players
     *
     * @return - A set of players
     */
    public Set<Player> getPlayers() {
        return this.players;
    }

    /**
     * Set NPC players
     *
     * @param players - New players
     * @return - This NPC object (useful for inline pattern)
     */
    public NPC setPlayers(Set<Player> players) {
        this.players = players;

        return this;
    }

    /**
     * Add a player
     *
     * @param player - Player to add
     * @return - This NPC object (useful for inline pattern)
     */
    public NPC addPlayer(Player player) {
        if (!this.players.contains(player)) {
            this.players.add(player);

            if (this.hologram != null) {
                this.hologram.addReceiver(player);
            }
        }
        return this;
    }

    /**
     * Remove a player
     *
     * @param player - Player to remove
     * @return - This NPC object (useful for inline pattern)
     */
    public NPC removePlayer(Player player) {
        this.players.remove(player);

        if (this.hologram != null) {
            this.hologram.removeReceiver(player);
        }

        return this;
    }

    /**
     * Get NPCs hologram
     *
     * @return - {@link Hologram} object
     */
    public Hologram getHologram() {
        return this.hologram;
    }

    /**
     * Set NPCs hologram
     *
     * @param hologram - New value
     * @return - This NPC object (useful for inline pattern)
     */
    public NPC setHologram(Hologram hologram) {
        this.hologram = hologram;

        return this;
    }

    /**
     * Set NPCs equipment
     *
     * @param slot - Equipment slot
     * @param itemStack - Item
     * @return - This NPC object (useful for inline pattern)
     */
    public NPC setEquipment(EnumItemSlot slot, ItemStack itemStack) {
        this.equipment.put(slot, itemStack);

        for (Player player : Bukkit.getOnlinePlayers()) {
            PacketUtil.sendPacket(player, this.getEquipmentPacket(slot, itemStack));
        }

        return this;
    }

    /**
     * Get NPCs interaction callback
     *
     * @return - {@link NPCInteractCallback} object
     */
    public NPCInteractCallback getInteractCallback() {
        return this.interactCallback;
    }

    /**
     * Set NPCs interaction callback
     *
     * @param interactCallback - New value
     * @return - This NPC object (useful for inline pattern)
     */
    public NPC setInteractCallback(NPCInteractCallback interactCallback) {
        this.interactCallback = interactCallback;

        return this;
    }

    public List<Packet<?>> getSpawnPackets() {
        final List<Packet<?>> packets = new ArrayList<>();

        packets.add(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, this));
        packets.add(new PacketPlayOutNamedEntitySpawn(this));
        packets.add(new PacketPlayOutEntityHeadRotation(this, (byte) (this.yaw * 256.0F / 360.0F)));

        for (Map.Entry<EnumItemSlot, ItemStack> entry : this.equipment.entrySet()) {
            packets.add(this.getEquipmentPacket(entry.getKey(), entry.getValue()));
        }

        return packets;
    }

    public List<Packet<?>> getDestroyPackets() {
        final List<Packet<?>> packets = new ArrayList<>();

        packets.add(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, this));
        packets.add(new PacketPlayOutEntityDestroy(this.getId()));

        return packets;
    }

    public List<Packet<?>> getRotationPackets(float yaw, float pitch) {
        final List<Packet<?>> packets = new ArrayList<>();

        packets.add(new PacketPlayOutEntity.PacketPlayOutEntityLook(this.getId(), (byte) (yaw * 256.0F / 360.0F), (byte) (pitch * 256.0F / 360.0F), false));
        packets.add(new PacketPlayOutEntityHeadRotation(this, (byte) (yaw * 256.0F / 360.0F)));

        return packets;
    }

    public Packet<?> getTeleportPacket() {
        return new PacketPlayOutEntityTeleport(this);
    }

    public Packet<?> getEquipmentPacket(EnumItemSlot slot, ItemStack itemStack) {
        return new PacketPlayOutEntityEquipment(this.getId(), slot.getSlot(), CraftItemStack.asNMSCopy(itemStack));
    }

    public Packet<?> getMetadataPacket() {
        return new PacketPlayOutEntityMetadata(this.getId(), this.getDataWatcher(), true);
    }

}
