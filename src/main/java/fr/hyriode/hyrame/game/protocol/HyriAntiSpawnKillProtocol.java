package fr.hyriode.hyrame.game.protocol;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.event.player.HyriGameDeathEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameRespawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by AstFaster
 * on 29/05/2022 at 16:58
 */
public class HyriAntiSpawnKillProtocol extends HyriGameProtocol {

    private final Map<UUID, BukkitTask> tasks;

    private final Options options;

    public HyriAntiSpawnKillProtocol(IHyrame hyrame, Options options) {
        super(hyrame, "anti_spawn_kill");
        this.options = options;
        this.tasks = new HashMap<>();

        HyriAPI.get().getEventBus().register(this);

        this.require(HyriDeathProtocol.class);
    }

    @Override
    void enable() {}

    @Override
    void disable() {
        for (BukkitTask task : this.tasks.values()) {
            task.cancel();
        }
    }

    @HyriEventHandler
    public void onRespawn(HyriGameRespawnEvent event) {
        final UUID playerId = event.getGamePlayer().getUUID();
        final BukkitTask task = Bukkit.getScheduler().runTaskLaterAsynchronously(this.getGame().getPlugin(), () -> this.tasks.remove(playerId), this.options.getTime());
        final BukkitTask old = this.tasks.put(playerId, task);

        if (old != null) {
            old.cancel();
        }
    }

    @HyriEventHandler
    public void onDeath(HyriGameDeathEvent event) {
        final BukkitTask task = this.tasks.remove(event.getGamePlayer().getUUID());

        if (task != null) {
            task.cancel();
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        final Entity damager = event.getDamager();
        final Entity entity = event.getEntity();

        if (entity instanceof Player && damager instanceof Player) {
            final Player player = (Player) entity;
            final Player hitter = (Player) damager;

            if (this.tasks.containsKey(hitter.getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            final BukkitTask task = this.tasks.remove(player.getUniqueId());

            if (task != null) {
                task.cancel();
            }
        }
    }

    public static class Options {

        /** The time before allowing other players to hit the player */
        private int time;
        /** If true, other players will be able to hit the player if the players hits another player*/
        private boolean hitReset;

        public Options(int time, boolean hitReset) {
            this.time = time;
            this.hitReset = hitReset;
        }

        public Options(int time) {
            this.time = time;
        }

        public int getTime() {
            return this.time;
        }

        public Options withTime(int time) {
            this.time = time;
            return this;
        }

        public boolean isHitReset() {
            return this.hitReset;
        }

        public Options withHitReset(boolean hitReset) {
            this.hitReset = hitReset;
            return this;
        }

    }

}
