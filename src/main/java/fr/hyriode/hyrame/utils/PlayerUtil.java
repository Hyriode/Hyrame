package fr.hyriode.hyrame.utils;

import com.mojang.authlib.GameProfile;
import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.packet.PacketUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutRespawn;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/02/2022 at 20:42
 */
public class PlayerUtil {

    public static void hidePlayer(Player player, boolean removeFromTabList) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            target.hidePlayer(player);

            if (!removeFromTabList) {
                PacketUtil.sendPacket(target, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) player).getHandle()));
            }
        }
    }

    public static void showPlayer(Player player) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            target.showPlayer(player);
        }
    }

    public static void reloadSkin(JavaPlugin plugin, Player player) {
        final EntityPlayer ep = ((CraftPlayer) player).getHandle();
        final PacketPlayOutPlayerInfo removeInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep);
        final PacketPlayOutPlayerInfo addInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep);
        final Location location = player.getLocation().clone();

        PacketUtil.sendPacket(player, removeInfo);

        new BukkitRunnable() {
            @Override
            public void run() {
                PacketUtil.sendPacket(player, new PacketPlayOutRespawn(ep.dimension, ep.getWorld().getDifficulty(), ep.getWorld().getWorldData().getType(), ep.playerInteractManager.getGameMode()));

                player.teleport(location);
                player.updateInventory();

                PacketUtil.sendPacket(player, addInfo);
            }
        }.runTaskLater(plugin, 1L);
    }

    public static GameProfile setName(Player player, String name) {
        final GameProfile profile = ((CraftPlayer) player).getProfile();

        try {
            final Field field = profile.getClass().getDeclaredField("name");

            field.setAccessible(true);
            field.set(profile, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return profile;
    }

    /**
     * Method used to get a {@link Player} by its name.<br>
     * This method is an alternative to {@link Bukkit#getPlayer(String)} as the Bukkit's method returns also assumptions.
     *
     * @param name The name of the player to get
     * @return A {@link Player} or <code>null</code> if a player with the given can't be found
     */
    public static Player getPlayer(String name) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }

    /**
     * Reset a {@link Player} (health, food level, experience etc.)
     *
     * @param player The {@link Player} to reset
     */
    public static void resetPlayer(Player player) {
        resetPlayer(player, false);
    }

    /**
     * Reset a {@link Player} (health, food level, experience etc.)
     *
     * @param player The {@link Player} to reset
     * @param hard If <code>true</code> the inventory of the player will also be cleared
     */
    public static void resetPlayer(Player player, boolean hard) {
        player.setFoodLevel(20);
        player.setHealth(20.0D);
        player.setLevel(0);
        player.setExp(0.0F);
        player.setFireTicks(0);

        resetPotionEffects(player);
        removeArrowsFromBody(player);

        if (hard) {
            player.closeInventory();

            player.setVelocity(new Vector(0.0D, 0.0D, 0.0D));

            resetPlayerInventory(player);
        }
    }

    /**
     * Reset all the potion effects of a given {@link Player}
     *
     * @param player The {@link Player} to reset
     */
    public static void resetPotionEffects(Player player) {
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    }

    /**
     * Reset the inventory of a {@link Player} (armor and items)
     *
     * @param player The {@link Player} to reset
     */
    public static void resetPlayerInventory(Player player) {
        final PlayerInventory inventory = player.getInventory();

        inventory.setArmorContents(null);
        inventory.clear();
    }

    /**
     * Add spectator abilities (fly, game mode, potion effects, etc.) to a given {@link Player}
     *
     * @param player The {@link Player} used to set abilities
     */
    public static void addSpectatorAbilities(Player player) {
        player.getInventory().setHeldItemSlot(0);

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false, false));
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    /**
     * Remove all arrows that are on the player body
     *
     * @param player The concerned {@link Player}
     */
    public static void removeArrowsFromBody(Player player) {
        ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) -1);
    }

    /**
     * Hide the armor of a player to another player
     *
     * @param player The concerned {@link Player}
     * @param target The target that will no longer see the player armor
     */
    public static void hideArmor(Player player, Player target) {
        for (int i = 1; i <= 4; i++) {
            PacketUtil.sendPacket(target, new PacketPlayOutEntityEquipment(player.getEntityId(), i, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR))));
        }
    }

    /**
     * Show the armor of a player to another player
     *
     * @param player The concerned {@link Player}
     * @param target The target that will see the player armor
     */
    public static void showArmor(Player player, Player target) {
        for (int i = 1; i <= 4; i++) {
            PacketUtil.sendPacket(target, new PacketPlayOutEntityEquipment(player.getEntityId(), i, CraftItemStack.asNMSCopy(player.getInventory().getArmorContents()[i - 1])));
        }
    }

    /**
     * Send given components to a player
     *
     * @param playerId The player id
     * @param components The components to send
     */
    public static void sendComponent(UUID playerId, BaseComponent... components) {
        ThreadUtil.ASYNC_EXECUTOR.execute(() -> HyriAPI.get().getPlayerManager().sendComponent(playerId, SerializerUtil.serializeComponent(components)));
    }

    /**
     * Send given components to a player
     *
     * @param player The player
     * @param components The components to send
     */
    public static void sendComponent(Player player, BaseComponent... components) {
        sendComponent(player.getUniqueId(), components);
    }

}
