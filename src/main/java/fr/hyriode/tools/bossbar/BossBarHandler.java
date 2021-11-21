package fr.hyriode.tools.bossbar;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
class BossBarHandler implements Listener {

    private final JavaPlugin plugin;

    public BossBarHandler(JavaPlugin plugin) {
        this.plugin = plugin;

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (BossBarManager.hasBar(player)) {
            BossBarManager.removeBar(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKick(PlayerKickEvent event) {
        final Player player = event.getPlayer();

        if (BossBarManager.hasBar(player)) {
            BossBarManager.removeBar(event.getPlayer());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        if (player.isOnline()) {
            if (BossBarManager.hasBar(player)) {
                BossBarManager.getBar(event.getPlayer()).updateMovement();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeleport(PlayerTeleportEvent event) {
        this.handleTeleport(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) {
        this.handleTeleport(event.getPlayer());
    }

    private void handleTeleport(Player player) {
        if (BossBarManager.hasBar(player)) {
            final BossBar bossBar = BossBarManager.getBar(player);

            BossBarManager.removeBar(player);

            new BukkitRunnable() {
                @Override
                public void run() {
                    BossBarManager.setBar(player, bossBar.getTitles(), bossBar.delay, bossBar.timeout, bossBar.updateProgressWithTimeout);
                }
            }.runTaskLater(this.plugin, 2);
        }
    }

}
