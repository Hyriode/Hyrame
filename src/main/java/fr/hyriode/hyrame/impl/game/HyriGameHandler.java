package fr.hyriode.hyrame.impl.game;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.IHyriGameManager;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.utils.block.BlockUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

import java.util.function.Consumer;
import java.util.function.Predicate;

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
        this.runActionOnGame(game -> event.setCancelled(true), game -> {
            final HyriGamePlayer gamePlayer = game.getPlayer((Player) event.getWhoClicked());

            if (gamePlayer == null) {
                return false;
            }

            return game.getState() != HyriGameState.PLAYING || gamePlayer.isSpectator();
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onItemSpawn(ItemSpawnEvent event) {
        this.runActionOnGame(game -> event.setCancelled(true), game -> game.getState() != HyriGameState.PLAYING);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        this.runActionOnGame(game -> {
            final Block block = event.getClickedBlock();
            final Action action = event.getAction();

            if (block != null) {
                final Material type = block.getType();

                if ((action == Action.PHYSICAL && BlockUtil.isPressurePlate(type)) || block.getState() instanceof InventoryHolder) {
                    event.setCancelled(true);
                }
            }
        }, game -> game.getState() != HyriGameState.PLAYING || game.getOutsideSpectator(event.getPlayer().getUniqueId()) != null || game.getPlayer(event.getPlayer()).isSpectator());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDrop(PlayerDropItemEvent event) {
        this.runActionOnGame(game -> event.setCancelled(true), game -> game.getState() != HyriGameState.PLAYING || game.getOutsideSpectator(event.getPlayer().getUniqueId()) != null || game.getPlayer(event.getPlayer()).isSpectator());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            this.runActionOnGame(game -> event.setCancelled(true), game -> {
                final HyriGamePlayer gamePlayer = game.getPlayer(event.getEntity().getUniqueId());

                if (gamePlayer == null) {
                    return false;
                }

                return game.getState() != HyriGameState.PLAYING || gamePlayer.isDead() ||gamePlayer.isSpectator();
            });
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        this.runActionOnGame(game -> event.setCancelled(true), game -> game.getState() != HyriGameState.PLAYING);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDamage(EntityDamageByEntityEvent event) {
        this.runActionOnGame(game -> {
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
        }, game -> {
            final HyriGamePlayer gamePlayer = game.getPlayer(event.getEntity().getUniqueId());

            if (gamePlayer == null) {
                return false;
            }

            return game.getState() == HyriGameState.PLAYING;
        });
    }

    private boolean cancelDamages(Player player, Player target) {
        final HyriGame<?> game = this.gameManager.getCurrentGame();
        final HyriGamePlayer gamePlayer = game.getPlayer(player);

        if (gamePlayer == null) {
            return true;
        }

        return (game.areInSameTeam(player, target) && !game.getPlayerTeam(player).isFriendlyFire()) || gamePlayer.isSpectator() || gamePlayer.isDead();
    }

    private void runActionOnGame(Consumer<HyriGame<?>> action) {
        final HyriGame<?> game = this.hyrame.getGameManager().getCurrentGame();

        if (game != null) {
            action.accept(game);
        }
    }

    private void runActionOnGame(Consumer<HyriGame<?>> action, Predicate<HyriGame<?>> condition) {
        this.runActionOnGame(game -> {
            if (condition.test(game)) {
                action.accept(game);
            }
        });
    }

}
