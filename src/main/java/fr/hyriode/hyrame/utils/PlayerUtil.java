package fr.hyriode.hyrame.utils;

import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/02/2022 at 20:42
 */
public class PlayerUtil {

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

        resetPotionEffects(player);
        removeArrowsFromBody(player);

        if (hard) {
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

}
