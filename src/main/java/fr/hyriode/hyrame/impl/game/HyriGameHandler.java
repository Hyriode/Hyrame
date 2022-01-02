package fr.hyriode.hyrame.impl.game;

import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.IHyriGameManager;
import fr.hyriode.hyrame.impl.Hyrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 09/09/2021 at 20:14
 */
class HyriGameHandler implements Listener {

    private final IHyriGameManager gameManager;

    private final Hyrame hyrame;

    public HyriGameHandler(Hyrame hyrame) {
        this.hyrame = hyrame;
        this.gameManager = hyrame.getGameManager();

        hyrame.getPlugin().getServer().getPluginManager().registerEvents(this, hyrame.getPlugin());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onClick(InventoryClickEvent event) {
        if (this.gameManager.getCurrentGame().getState() != HyriGameState.PLAYING) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        this.gameManager.getCurrentGame().handleLogin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent event) {
        this.gameManager.getCurrentGame().handleLogout(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (this.gameManager.getCurrentGame().getState() == HyriGameState.PLAYING) {
            if (event.getEntity() instanceof Player) {
                final Player target = (Player) event.getEntity();

                if (event.getDamager() instanceof Player) {
                    event.setCancelled(this.cancelDamages((Player) event.getDamager(), target));
                } else if (event.getDamager() instanceof Projectile) {
                    final Projectile projectile = (Projectile) event.getDamager();

                    if (projectile.getShooter() instanceof Player) {
                        event.setCancelled(this.cancelDamages((Player) projectile.getShooter(), target));
                    }
                }
            }
        } else {
            event.setCancelled(true);
        }
    }

    private boolean cancelDamages(Player player, Player target) {
        return this.gameManager.getCurrentGame().areInSameTeam(player, target);
    }

}
