package fr.hyriode.hyrame.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by AstFaster
 * on 06/06/2022 at 13:02
 */
public class BossBarManager {

    /** The {@linkplain Map map} with all {@linkplain BossBar boss bars} linked to players */
    private static final Map<UUID, BossBar> BARS = new ConcurrentHashMap<>();

    /** The static instance of a {@link JavaPlugin} */
    private static JavaPlugin plugin;

    /**
     * Init the boss bar manager by starting animation process and providing a {@link JavaPlugin} instance
     *
     * @param plugin The {@link JavaPlugin} instance
     */
    public static void init(JavaPlugin plugin) {
        if (BossBarManager.plugin != null) {
            throw new IllegalStateException("BossBar manager is already initialized!");
        }

        BossBarManager.plugin = plugin;

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (BossBar bossBar : BARS.values()) {
                final BossBarAnimation animation = bossBar.getAnimation();

                if (animation != null) {
                    animation.onTick();
                }
            }
        }, 0L, 1L);
    }

    /**
     * Set the boss bar of a player
     *
     * @param player The player
     * @param text The default text to put on the bar
     * @param progress The progress of the bar (from 0.0F to 1.0F)
     * @return The created {@link BossBar}
     */
    public static BossBar setBar(Player player, String text, float progress) {
        removeBar(player);

        final BossBar bossBar = new BossBar(player, plugin, text);

        bossBar.spawn();
        bossBar.setProgress(progress);

        BARS.put(player.getUniqueId(), bossBar);

        return bossBar;
    }

    /**
     * Get the bar of a given player
     *
     * @param player The player
     * @return The {@linkplain BossBar boss bar} of the player; or <code>null</code>
     */
    public static BossBar getBar(Player player) {
        return BARS.get(player.getUniqueId());
    }

    /**
     * Remove the boss bar of a given player
     *
     * @param player The player
     */
    public static void removeBar(Player player) {
        final BossBar bossBar = BARS.remove(player.getUniqueId());

        if (bossBar == null) {
            return;
        }

        bossBar.destroy();
    }

    /**
     * Check if a given player has a boss bar
     *
     * @param player The player to check
     * @return <code>true</code> if he has a boss bar
     */
    public static boolean hasBar(Player player) {
        return BARS.containsKey(player.getUniqueId());
    }

    /**
     * Get all the boss bars
     *
     * @return A map of {@link BossBar} related to their owner
     */
    public static Map<UUID, BossBar> getBars() {
        return BARS;
    }

}
