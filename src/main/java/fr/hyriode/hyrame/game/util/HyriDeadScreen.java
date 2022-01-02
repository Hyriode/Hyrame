package fr.hyriode.hyrame.game.util;

import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 01/01/2022 at 19:37
 */
public class HyriDeadScreen extends BukkitRunnable {

    private static final HyriLanguageMessage DEAD = new HyriLanguageMessage("")
            .addValue(HyriLanguage.EN, "dead")
            .addValue(HyriLanguage.FR, "mort");

    private static final HyriLanguageMessage RESPAWN = new HyriLanguageMessage("")
            .addValue(HyriLanguage.EN, "Respawn in")
            .addValue(HyriLanguage.FR, "Réapparition dans");

    private static final HyriLanguageMessage SECOND = new HyriLanguageMessage("")
            .addValue(HyriLanguage.EN, "second")
            .addValue(HyriLanguage.FR, "seconde");

    private static final HyriLanguageMessage SECONDS = new HyriLanguageMessage("")
            .addValue(HyriLanguage.EN, "seconds")
            .addValue(HyriLanguage.FR, "secondes");

    /** The Spigot plugin */
    private final JavaPlugin plugin;
    /** The player that will have the screen*/
    private final Player player;
    /** The time before removing the screen */
    private int time;
    /** The callback to execute after */
    private final Runnable callback;

    /**
     * Constructor of {@link HyriDeadScreen}
     *
     * @param plugin The plugin
     * @param player The player
     * @param time The time before removing
     * @param callback The callback to fire
     */
    private HyriDeadScreen(JavaPlugin plugin, Player player, int time, Runnable callback) {
        this.plugin = plugin;
        this.player = player;
        this.time = time;
        this.callback = callback;
    }

    @Override
    public void run() {
        if (time <= 0) {
            this.cancel();

            new BukkitRunnable() {
                @Override
                public void run() {
                    callback.run();
                }
            }.runTask(this.plugin);
        } else {
            final String title = ChatColor.DARK_AQUA + Symbols.ROTATED_SQUARE + " " + DEAD.getForPlayer(this.player).toUpperCase() + " " + Symbols.ROTATED_SQUARE;

            if (time == 1) {
                Title.sendTitle(this.player, title, ChatColor.AQUA + RESPAWN.getForPlayer(this.player) + ChatColor.WHITE + " " + this.time + ChatColor.AQUA + " " + SECOND.getForPlayer(this.player), 0, 20, 0);
            } else {
                Title.sendTitle(this.player, title, ChatColor.AQUA + RESPAWN.getForPlayer(this.player) + ChatColor.WHITE + " " + this.time + ChatColor.AQUA + " " + SECONDS.getForPlayer(this.player), 0, 30, 0);
            }
        }
        this.time--;
    }

    public static HyriDeadScreen create(JavaPlugin plugin, Player player, int time, Runnable callback) {
        final HyriDeadScreen screen = new HyriDeadScreen(plugin, player, time, callback);

        screen.runTaskTimerAsynchronously(plugin, 0L, 20L);

        return screen;
    }

}
