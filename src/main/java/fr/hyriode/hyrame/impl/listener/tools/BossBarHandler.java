package fr.hyriode.hyrame.impl.listener.tools;

import fr.hyriode.hyrame.bossbar.BossBar;
import fr.hyriode.hyrame.bossbar.BossBarAnimation;
import fr.hyriode.hyrame.bossbar.BossBarManager;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.listener.HyriListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class BossBarHandler extends HyriListener<HyramePlugin> {

    public BossBarHandler(HyramePlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        BossBarManager.removeBar(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKick(PlayerKickEvent event) {
        BossBarManager.removeBar(event.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final BossBar bossBar = BossBarManager.getBar(event.getPlayer());

        if (bossBar != null) {
            bossBar.updateMovement();
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
        final BossBar bossBar = BossBarManager.getBar(player);

        if (bossBar == null) {
            return;
        }

        BossBarManager.removeBar(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                final BossBar newBar = BossBarManager.setBar(player, bossBar.getText(), bossBar.getProgress());
                final BossBarAnimation animation = bossBar.getAnimation();

                if (animation != null) {
                    animation.disable();

                    newBar.applyAnimation(animation);
                }
            }
        }.runTaskLaterAsynchronously(this.plugin, 2L);

    }

}
