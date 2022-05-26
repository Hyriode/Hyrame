package fr.hyriode.hyrame.game.protocol;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 11/03/2022 at 21:03
 */
public class HyriLastHitterProtocol extends HyriGameProtocol {

    private Map<UUID, Set<LastHitter>> lastHitters = new ConcurrentHashMap<>();

    private long removeTime;

    private final JavaPlugin plugin;

    public HyriLastHitterProtocol(IHyrame hyrame, JavaPlugin plugin, long removeTime) {
        super(hyrame, "last_hitter");
        this.plugin = plugin;
        this.removeTime = removeTime;
    }

    @Override
    void enable() {}

    @Override
    void disable() {
        this.lastHitters.clear();
        this.lastHitters = null;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            final Player player = (Player) event.getEntity();
            final UUID playerId = player.getUniqueId();
            final Player hitter = (Player) event.getDamager();
            final HyriGamePlayer gamePlayer = this.getGamePlayer(hitter);

            if (this.getGame().areInSameTeam(player, hitter) && !gamePlayer.getTeam().isFriendlyFire()) {
                return;
            }

            if (!gamePlayer.isSpectator()) {
                final Set<LastHitter> hitters = this.lastHitters.getOrDefault(playerId, new HashSet<>());
                LastHitter lastHitter = this.getLastHitter(player, hitter);

                if (lastHitter == null) {
                    lastHitter = new LastHitter(hitter.getUniqueId());
                }

                lastHitter.addHit();
                hitters.add(lastHitter);

                this.lastHitters.put(playerId, hitters);

                this.removePlayerAfter(player, lastHitter);
            }
        }
    }

    private void removePlayerAfter(Player player, LastHitter initial) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            final Set<LastHitter> hitters = this.lastHitters.get(player.getUniqueId());

            if (hitters != null && initial != null) {
                final LastHitter lastHitter = this.getLastHitter(player, initial.asPlayer());

                if (lastHitter != null) {
                    if (!lastHitter.getIdentifier().equals(initial.getIdentifier())) {
                        return;
                    }

                    hitters.remove(lastHitter);

                    if (hitters.size() == 0) {
                        this.removeLastHitters(player);
                    } else {
                        this.lastHitters.put(player.getUniqueId(), hitters);
                    }
                }
            }
        }, this.removeTime);
    }

    public LastHitter getLastHitter(Player player, Player hitter) {
        if (hitter == null || player == null) {
            return null;
        }

        final Set<LastHitter> lastHitters = this.lastHitters.get(player.getUniqueId());

        if (lastHitters != null) {
            return lastHitters.stream().filter(h -> h.getUniqueId().equals(hitter.getUniqueId())).findFirst().orElse(null);
        }
        return null;
    }

    public List<LastHitter> getLastHitters(Player player) {
        final Set<LastHitter> lastHitters = this.lastHitters.get(player.getUniqueId());

        if (lastHitters != null) {
            return lastHitters.stream().sorted((o1, o2) -> o2.getHits() - o1.getHits()).collect(Collectors.toList());
        }
        return null;
    }

    public void removeLastHitters(Player player) {
        this.lastHitters.remove(player.getUniqueId());
    }

    public Map<UUID, Set<LastHitter>> getLastHittersMap() {
        return this.lastHitters;
    }

    public long getRemoveTime() {
        return this.removeTime;
    }

    public void setRemoveTime(long removeTime) {
        this.removeTime = removeTime;
    }

    public class LastHitter {

        private final UUID identifier;
        private final UUID player;
        private int hits;

        public LastHitter(UUID player) {
            this.identifier = UUID.randomUUID();
            this.player = player;
            this.hits = 1;
        }

        UUID getIdentifier() {
            return this.identifier;
        }

        public UUID getUniqueId() {
            return this.player;
        }

        public Player asPlayer() {
            return Bukkit.getPlayer(this.player);
        }

        public HyriGamePlayer asGamePlayer() {
            return hyrame.getGameManager().getCurrentGame().getPlayer(this.player);
        }

        public Integer getHits() {
            return this.hits;
        }

        public void addHit() {
            this.hits++;
        }

    }

}
