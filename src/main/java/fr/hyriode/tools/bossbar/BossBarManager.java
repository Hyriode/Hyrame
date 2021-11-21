package fr.hyriode.tools.bossbar;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class BossBarManager {

    private static final Map<Player, BossBar> bossBars = new ConcurrentHashMap<>();

    private static JavaPlugin plugin;

    public BossBarManager(JavaPlugin plugin) {
        BossBarManager.plugin = plugin;

        new BossBarHandler(plugin);
    }

    public static BossBar getBar(Player player) {
        return bossBars.get(player);
    }

    public static BossBar setBar(Player player, List<String> titles, int delay, int timeout, boolean updateProgressWithTimeout) {
        final BossBar bossBar = new BossBar(plugin, player, titles, delay, timeout, updateProgressWithTimeout);

        bossBar.spawn();

        if (hasBar(player)) {
            removeBar(player);
        }

        bossBars.put(player, bossBar);

        return bossBar;
    }

    public static BossBar setBar(Player player,List<String> titles, int timeout, boolean updateProgressWithTimeout) {
        return setBar(player, titles, 0, timeout, updateProgressWithTimeout);
    }

    public static BossBar setBar(Player player,List<String> titles, int timeout) {
        return setBar(player, titles, 0, timeout, false);
    }

    public static BossBar setBar(Player player, String title, int timeout, boolean updateProgressWithTimeout) {
        return setBar(player, Collections.singletonList(title), 0, timeout, updateProgressWithTimeout);
    }

    public static BossBar setBar(Player player, String title, int timeout) {
        return setBar(player, Collections.singletonList(title), 0, timeout, false);
    }

    public static BossBar setBar(Player player, String title) {
        return setBar(player, Collections.singletonList(title), 0, 0, false);
    }

    public static void removeBar(Player player) {
        if (hasBar(player)) {
            bossBars.get(player).destroy();
            bossBars.remove(player);
        }
    }

    public static boolean hasBar(Player player) {
        return bossBars.containsKey(player);
    }

    public static List<String> getBarTitles(Player player) {
        if (hasBar(player)) {
            return getBar(player).getTitles();
        }
        throw new NullPointerException();
    }

    public static void setBarTitles(Player player, List<String> titles) {
        if (hasBar(player)) {
            getBar(player).setTitles(titles);
        }
    }

    public static void setBarTitle(Player player, String title) {
        setBarTitles(player, Collections.singletonList(title));
    }

    public static float getBarProgress(Player player) {
        if (hasBar(player)) {
            return getBar(player).getProgress();
        }
        throw new NullPointerException();
    }

    public static void setBarProgress(Player player, float progress) {
        if (hasBar(player)) {
            getBar(player).setProgress(progress);
        }
    }

}
