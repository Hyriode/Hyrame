package fr.hyriode.hyrame.impl.game;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.utils.block.BlockUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.projectiles.ProjectileSource;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 09/09/2021 at 20:14
 */
class GameHandler implements Listener {

    private final HyriGame<?> game;

    public GameHandler() {
        this.game = IHyrame.get().getGame();

        IHyrame.get().getPlugin().getServer().getPluginManager().registerEvents(this, IHyrame.get().getPlugin());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final HyriGamePlayer gamePlayer = this.game.getPlayer(player);

        if (this.game.getState() != HyriGameState.PLAYING ||
                (gamePlayer != null && gamePlayer.isSpectator()) ||
                game.getSpectator(player.getUniqueId()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onItemSpawn(ItemSpawnEvent event) {
        if (this.game.getState() != HyriGameState.PLAYING) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final HyriGamePlayer gamePlayer = this.game.getPlayer(player);

        if (this.game.getState() != HyriGameState.PLAYING ||
                game.getSpectator(player.getUniqueId()) != null ||
                (gamePlayer != null && gamePlayer.isSpectator())) {
            final Block block = event.getClickedBlock();
            final Action action = event.getAction();

            if (block != null) {
                final Material type = block.getType();

                if ((action == Action.PHYSICAL && BlockUtil.isPressurePlate(type)) || block.getState() instanceof InventoryHolder || block.getType() == Material.TRAP_DOOR) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDrop(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final HyriGamePlayer gamePlayer = this.game.getPlayer(player);

        if (this.game.getState() != HyriGameState.PLAYING ||
                game.getSpectator(player.getUniqueId()) != null ||
                (gamePlayer != null && gamePlayer.isSpectator())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            final HyriGamePlayer gamePlayer = this.game.getPlayer(player);

            if (game.getState() != HyriGameState.PLAYING ||
                    (gamePlayer != null && (gamePlayer.isDead() || gamePlayer.isSpectator())) ||
                    this.game.getSpectator(player.getUniqueId()) != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.game.getState() != HyriGameState.PLAYING) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDamage(EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        final HyriGamePlayer gamePlayer = this.game.getPlayer(entity.getUniqueId());

        if (gamePlayer != null && game.getState() == HyriGameState.PLAYING) {
            final Player target = (Player) entity;
            final Entity damager = event.getDamager();

            if (damager instanceof Player) {
                event.setCancelled(this.cancelDamages((Player) damager, target));
            } else if (damager instanceof Projectile) {
                final Projectile projectile = (Projectile) damager;
                final ProjectileSource shooter = projectile.getShooter();

                if (projectile.getShooter() instanceof Player) {
                    event.setCancelled(this.cancelDamages((Player) shooter, target));
                }
            }
        }
    }

    private boolean cancelDamages(Player player, Player target) {
        final HyriGamePlayer gamePlayer = this.game.getPlayer(player);

        if (gamePlayer == null) {
            return true;
        }
        return (this.game.areInSameTeam(player, target) && !gamePlayer.getTeam().isFriendlyFire()) || gamePlayer.isSpectator() || gamePlayer.isDead();
    }

}
