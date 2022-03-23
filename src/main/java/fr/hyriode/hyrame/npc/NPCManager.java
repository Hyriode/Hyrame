package fr.hyriode.hyrame.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.hyriode.hyrame.hologram.HyriHologram;
import fr.hyriode.hyrame.utils.PacketUtil;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.function.Consumer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class HyriNPCManager {

    /** Some constants */
    private static final String NPC_TEAM_NAME = "NPC";
    private static final String NPC_NAME_PREFIX = "[" + NPC_TEAM_NAME + "] ";

    /** All NPCs */
    private static final Map<HyriNPC, HyriHologram> NPCS = new HashMap<>();

    /** Skin Redis key */
    private static String cacheSkinRedisKey;

    /** Spigot plugin */
    private static JavaPlugin plugin;

    /**
     * Constructor of {@link HyriNPCManager}
     *
     * @param plugin Spigot plugin
     * @param cacheSkinRedisKey Skin Redis key
     */
    public HyriNPCManager(JavaPlugin plugin, String cacheSkinRedisKey) {
        HyriNPCManager.plugin = plugin;
        HyriNPCManager.cacheSkinRedisKey = cacheSkinRedisKey;
    }

    /**
     * Create a NPC
     *
     * @param location - NPCs location
     * @param skin - NPCs skin
     * @param hologramLines - NPCs hologram lines
     * @return - {@link HyriNPC} object
     */
    public static HyriNPC createNPC(Location location, HyriNPCSkin skin, List<String> hologramLines) {
        return createNPC(location, skin, hologramLines.toArray(new String[0]));
    }
    /**
     * Create a NPC
     *
     * @param location - NPCs location
     * @param skin - NPCs skin
     * @param hologramLines - NPCs hologram lines
     * @return - {@link HyriNPC} object
     */

    public static HyriNPC createNPC(Location location, HyriNPCSkin skin, String[] hologramLines) {
        final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), ChatColor.DARK_GRAY + NPC_NAME_PREFIX + UUID.randomUUID().toString().split("-")[0]);

        gameProfile.getProperties().put("textures", new Property("textures", skin.getTextureData(), skin.getTextureSignature()));

        return createNPC(location, gameProfile, hologramLines);
    }

    /**
     * Create a NPC
     *
     * @param location - NPCs location
     * @param skinOwner - NPCs skin owner
     * @param hologramLines - NPCs hologram lines
     * @return - {@link HyriNPC} object
     */
    public static HyriNPC createNPC(Location location, String skinOwner, List<String> hologramLines) {
        return createNPC(location, skinOwner, hologramLines.toArray(new String[0]));
    }

    /**
     * Create a NPC
     *
     * @param location - NPCs location
     * @param skinOwner - NPCs skin owner
     * @param hologramLines - NPCs hologram lines
     * @return - {@link HyriNPC} object
     */
    public static HyriNPC createNPC(Location location, String skinOwner, String[] hologramLines) {
        final GameProfile gameProfile = new HyriNPCProfileLoader(UUID.randomUUID(), ChatColor.DARK_GRAY + NPC_NAME_PREFIX + UUID.randomUUID().toString().split("-")[0], skinOwner, cacheSkinRedisKey).loadProfile();

        return createNPC(location, gameProfile, hologramLines);
    }

    /**
     * Create a NPC
     *
     * @param location - NPCs location
     * @param hologramLines - NPCs hologram lines
     * @return - {@link HyriNPC} object
     */
    public static HyriNPC createNPC(Location location, String[] hologramLines) {
        return createNPC(location, new GameProfile(UUID.randomUUID(), ChatColor.DARK_GRAY + NPC_NAME_PREFIX + UUID.randomUUID().toString().split("-")[0]), hologramLines);
    }

    /**
     * Create a NPC
     *
     * @param location - NPCs location
     * @return - {@link HyriNPC} object
     */
    public static HyriNPC createNPC(Location location) {
        return createNPC(location, new GameProfile(UUID.randomUUID(), ChatColor.DARK_GRAY + NPC_NAME_PREFIX + UUID.randomUUID().toString().split("-")[0]), new String[]{});
    }

    /**
     * Create a NPC
     *
     * @param location - NPCs location
     * @param gameProfile - NPCs profile
     * @param hologramLines - NPCs hologram lines
     * @return - {@link HyriNPC} object
     */
    private static HyriNPC createNPC(Location location, GameProfile gameProfile, String[] hologramLines) {
        final World world = ((CraftWorld) location.getWorld()).getHandle();
        final HyriNPC npc = new HyriNPC(plugin, location, world, gameProfile);
        final Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

        Team npcTeam = null;
        for (Team team : scoreboard.getTeams()) {
            if (team.getName().equals(NPC_TEAM_NAME)) {
                npcTeam = team;
            }
        }

        if (npcTeam == null) {
            npcTeam = scoreboard.registerNewTeam(NPC_TEAM_NAME);
        }

        npcTeam.setNameTagVisibility(NameTagVisibility.NEVER);
        npcTeam.addEntry(npc.getName());

        npc.getDataWatcher().watch(10, (byte) 127);

        sendMetadataNPC(npc);

        if (hologramLines != null) {
            final HyriHologram hologram = new HyriHologram.Builder(plugin, npc.getLocation().clone().add(0.0D, 1.8D, 0.0D))
                    .withLinesAsString(Arrays.asList(hologramLines))
                    .build();

            npc.setHologram(hologram);
        }

        if (!existsNPC(npc)) {
            NPCS.put(npc, npc.getHologram());
        }

        return npc;
    }

    /**
     * Send a NPC to a player
     *
     * @param player - Player
     * @param npc - NPC to send
     */
    public static void sendNPC(Player player, HyriNPC npc) {
        final Consumer<Player> sendConsumer = p -> {
            for (Packet<?> packet : npc.getSpawnPackets()) {
                PacketUtil.sendPacket(player, packet);
            }

            npc.addPlayer(p);

            new BukkitRunnable() {
                @Override
                public void run() {
                    PacketUtil.sendPacket(player, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
                }
            }.runTaskLater(plugin, 20L);
        };

        if (npc.isShowingToAll()) {
            sendConsumer.accept(player);
        } else {
            if (npc.getPlayers().contains(player)) {
                sendConsumer.accept(player);
            }
        }
    }

    /**
     * Send a NPC to all players
     *
     * @param npc - NPC to send
     */
    public static void sendNPC(HyriNPC npc) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendNPC(player, npc);
        }
    }

    /**
     * Remove a NPC from a player
     *
     * @param player - Player
     * @param npc - NPC to remove
     */
    public static void removeNPC(Player player, HyriNPC npc) {
        for (Packet<?> packet : npc.getDestroyPackets()) {
            PacketUtil.sendPacket(player, packet);
        }
    }

    /**
     * Remove a NPC from all players
     *
     * @param npc - NPC to remove
     */
    public static void removeNPC(HyriNPC npc) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            removeNPC(player, npc);
        }

        NPCS.remove(npc);

        npc.getHologram().destroy();
    }

    /**
     * Send NPCs metadata to a player
     *
     * @param player - Player
     * @param npc - NPC
     */
    public static void sendMetadataNPC(Player player, HyriNPC npc) {
        PacketUtil.sendPacket(player, npc.getMetadataPacket());
    }

    /**
     * Send NPCs metadata to all players
     *
     * @param npc - NPC
     */
    public static void sendMetadataNPC(HyriNPC npc) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendMetadataNPC(player, npc);
        }
    }

    /**
     * Set NPCs skin
     *
     * @param npc - NPC
     * @param owner - Skin owner
     */
    public static void setSkinNPC(HyriNPC npc, String owner) {
        removeNPC(npc);

        final GameProfile profile = new HyriNPCProfileLoader(npc.getProfile().getId(), npc.getName(), owner, cacheSkinRedisKey).loadProfileFromRedis();

        npc.getProfile().getProperties().putAll(profile.getProperties());

        npc.getDataWatcher().watch(10, (byte) 127);

        sendMetadataNPC(npc);
        sendNPC(npc);
    }

    /**
     * Get a NPC by its name
     *
     * @param name - NPCs name
     * @return - {@link HyriNPC} object
     */
    public static HyriNPC getNPC(String name) {
        for (HyriNPC npc : NPCS.keySet()) {
            if (npc.getName().equals(name)) {
                return npc;
            }
        }
        return null;
    }

    /**
     * Check if a NPC exists with the given name
     *
     * @param name - NPCs name
     * @return - <code>true</code> if yes
     */
    public static boolean hasNPCWithName(String name) {
        for (HyriNPC npc : NPCS.keySet()) {
            if (npc.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a NPC already exists
     *
     * @param npc - NPC
     * @return - <code>true</code> if yes
     */
    public static boolean existsNPC(HyriNPC npc) {
        for (HyriNPC n : NPCS.keySet()) {
            if (n.getLocation().equals(npc.getLocation()) && n.getHologram() == npc.getHologram() && Arrays.equals(n.getEquipment(), npc.getEquipment())) {
                return true;
            }
        }
        return false;
    }

    public static Map<HyriNPC, HyriHologram> getNPCs() {
        return NPCS;
    }

}
