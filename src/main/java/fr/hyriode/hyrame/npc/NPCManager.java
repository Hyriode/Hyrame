package fr.hyriode.hyrame.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.hyriode.api.util.Skin;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.hologram.Hologram;
import fr.hyriode.hyrame.packet.PacketUtil;
import fr.hyriode.hyrame.utils.player.ProfileLoader;
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
public class NPCManager {

    /** Some constants */
    private static final String NPC_TEAM_NAME = "zNPC";
    private static final String NPC_NAME_PREFIX = "[NPC] ";

    /** All NPCs */
    private static final List<NPC> NPCS = new ArrayList<>();

    /**
     * Create a NPC
     *
     * @param location - NPCs location
     * @param skin - NPCs skin
     * @param hologramLines - NPCs hologram lines
     * @return - {@link NPC} object
     */
    public static NPC createNPC(Location location, Skin skin, String[] hologramLines) {
        final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), ChatColor.DARK_GRAY + NPC_NAME_PREFIX + UUID.randomUUID().toString().split("-")[0]);

        gameProfile.getProperties().put("textures", new Property("textures", skin.getTextureData(), skin.getTextureSignature()));

        return createNPC(location, gameProfile, hologramLines);
    }

    /**
     * Create a NPC
     *
     * @param location - NPCs location
     * @param skin - NPCs skin
     * @param hologramLines - NPCs hologram lines
     * @return - {@link NPC} object
     */
    public static NPC createNPC(Location location, Skin skin, List<String> hologramLines) {
        return createNPC(location, skin, hologramLines.toArray(new String[0]));
    }


    /**
     * Create a NPC
     *
     * @param location - NPCs location
     * @param skinOwner - NPCs skin owner
     * @param hologramLines - NPCs hologram lines
     * @return - {@link NPC} object
     */
    public static NPC createNPC(Location location, String skinOwner, String[] hologramLines) {
        final GameProfile gameProfile = new ProfileLoader(UUID.randomUUID(), ChatColor.DARK_GRAY + NPC_NAME_PREFIX + UUID.randomUUID().toString().split("-")[0], skinOwner).loadProfile();

        return createNPC(location, gameProfile, hologramLines);
    }

    /**
     * Create a NPC
     *
     * @param location - NPCs location
     * @param skinOwner - NPCs skin owner
     * @param hologramLines - NPCs hologram lines
     * @return - {@link NPC} object
     */
    public static NPC createNPC(Location location, String skinOwner, List<String> hologramLines) {
        return createNPC(location, skinOwner, hologramLines.toArray(new String[0]));
    }

    /**
     * Create a NPC
     *
     * @param location - NPCs location
     * @param hologramLines - NPCs hologram lines
     * @return - {@link NPC} object
     */
    public static NPC createNPC(Location location, String[] hologramLines) {
        return createNPC(location, new GameProfile(UUID.randomUUID(), ChatColor.DARK_GRAY + NPC_NAME_PREFIX + UUID.randomUUID().toString().split("-")[0]), hologramLines);
    }

    /**
     * Create a NPC
     *
     * @param location - NPCs location
     * @return - {@link NPC} object
     */
    public static NPC createNPC(Location location) {
        return createNPC(location, new GameProfile(UUID.randomUUID(), ChatColor.DARK_GRAY + NPC_NAME_PREFIX + UUID.randomUUID().toString().split("-")[0]), new String[]{});
    }

    /**
     * Create a NPC
     *
     * @param location - NPCs location
     * @param gameProfile - NPCs profile
     * @param hologramLines - NPCs hologram lines
     * @return - {@link NPC} object
     */
    private static NPC createNPC(Location location, GameProfile gameProfile, String[] hologramLines) {
        final World world = ((CraftWorld) location.getWorld()).getHandle();
        final NPC npc = new NPC(IHyrame.get().getPlugin(), location, world, gameProfile);
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
            final Hologram hologram = new Hologram.Builder(IHyrame.get().getPlugin(), npc.getLocation().clone().add(0.0D, 1.5D, 0.0D))
                    .withLinesAsString(Arrays.asList(hologramLines))
                    .build();

            npc.setHologram(hologram);
        }

        if (!existsNPC(npc)) {
            NPCS.add(npc);
        }

        return npc;
    }

    /**
     * Send a NPC to a player
     *
     * @param player - Player
     * @param npc - NPC to send
     */
    public static void sendNPC(Player player, NPC npc) {
        final Consumer<Player> sendConsumer = p -> {
            npc.spawnFor(p);

            new BukkitRunnable() {
                @Override
                public void run() {
                    PacketUtil.sendPacket(player, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
                }
            }.runTaskLater(IHyrame.get().getPlugin(), 3 * 20L);
        };

        if (npc.isShowingToAll()) {
            sendConsumer.accept(player);
        } else if (npc.getPlayers().contains(player)) {
            sendConsumer.accept(player);
        }
    }

    /**
     * Send a NPC to all players
     *
     * @param npc - NPC to send
     */
    public static void sendNPC(NPC npc) {
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
    public static void removeNPC(Player player, NPC npc) {
        if (npc == null) {
            return;
        }

        for (Packet<?> packet : npc.getDestroyPackets()) {
            PacketUtil.sendPacket(player, packet);
        }
    }

    /**
     * Remove a NPC from all players
     *
     * @param npc - NPC to remove
     */
    public static void removeNPC(NPC npc) {
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
    public static void sendMetadataNPC(Player player, NPC npc) {
        PacketUtil.sendPacket(player, npc.getMetadataPacket());
    }

    /**
     * Send NPCs metadata to all players
     *
     * @param npc - NPC
     */
    public static void sendMetadataNPC(NPC npc) {
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
    public static void setSkinNPC(NPC npc, String owner) {
        removeNPC(npc);

        final GameProfile profile = new ProfileLoader(npc.getProfile().getId(), npc.getName(), owner).loadProfile();

        npc.getProfile().getProperties().putAll(profile.getProperties());
        npc.getDataWatcher().watch(10, (byte) 127);

        sendMetadataNPC(npc);
        sendNPC(npc);
    }

    /**
     * Check if a NPC already exists
     *
     * @param npc - NPC
     * @return - <code>true</code> if yes
     */
    public static boolean existsNPC(NPC npc) {
        for (NPC n : NPCS) {
            if (n.getLocation().equals(npc.getLocation()) && n.getHologram() == npc.getHologram() && Arrays.equals(n.getEquipment(), npc.getEquipment())) {
                return true;
            }
        }
        return false;
    }

    public static List<NPC> getNPCs() {
        return NPCS;
    }

}
