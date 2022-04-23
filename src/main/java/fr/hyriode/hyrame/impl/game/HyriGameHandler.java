package fr.hyriode.hyrame.impl.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.IHyriGameManager;
import fr.hyriode.hyrame.impl.Hyrame;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
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
        this.runActionOnGame(game -> event.setCancelled(true), game -> game.getState() != HyriGameState.PLAYING || game.getPlayer((Player) event.getWhoClicked()).isSpectator());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        this.runActionOnGame(game -> {
            final Block block = event.getClickedBlock();

            if (block != null && block.getState() instanceof InventoryHolder && event.getAction() != Action.LEFT_CLICK_BLOCK) {
                event.setCancelled(true);
            }
        }, game -> game.getState() != HyriGameState.PLAYING || game.getPlayer(event.getPlayer()).isSpectator());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (HyriAPI.get().getConfiguration().isDevEnvironment()) {
            this.runActionOnGame(game -> {
                if (!game.getState().isAccessible()) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Partie en cours.");
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDrop(PlayerDropItemEvent event) {
        this.runActionOnGame(game -> event.setCancelled(true), game -> game.getState() != HyriGameState.PLAYING || game.getPlayer(event.getPlayer()).isSpectator());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            this.runActionOnGame(game -> event.setCancelled(true), game -> game.getState() != HyriGameState.PLAYING || game.getPlayer(event.getEntity().getUniqueId()).isDead() || game.getPlayer(event.getEntity().getUniqueId()).isSpectator());
        }
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
        }, game -> game.getState() == HyriGameState.PLAYING);
    }

    private boolean cancelDamages(Player player, Player target) {
        final HyriGame<?> game = this.gameManager.getCurrentGame();

        return (game.areInSameTeam(player, target) && !game.getPlayerTeam(player).isFriendlyFire()) || game.getPlayer(player).isSpectator();
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
