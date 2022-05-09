package fr.hyriode.hyrame.game.protocol;

import com.google.common.collect.Sets;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 11/03/2022 at 21:03
 */
public class HyriLastHitterProtocol extends HyriGameProtocol {

    private Map<Player, Set<LastHitter>> lastHitters = new ConcurrentHashMap<>();

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
            final Player hitter = (Player) event.getDamager();
            final HyriGamePlayer gamePlayer = this.getGamePlayer(hitter);

            if (this.getGame().areInSameTeam(player, hitter) && !gamePlayer.getTeam().isFriendlyFire()) {
                return;
            }

            if (!gamePlayer.isSpectator()) {
                Set<LastHitter> hitters = this.lastHitters.get(player);

                if (hitters == null) {
                    hitters = Sets.newConcurrentHashSet();
                }

                final LastHitter lastHitter = this.getLastHitter(player, hitter);

                if (lastHitter != null) {
                    lastHitter.addHit();
                } else {
                    hitters.add(new LastHitter(hitter));
                }

                this.lastHitters.put(player, hitters);

                this.removePlayerAfter(player, hitter);
            }
        }
    }

    private void removePlayerAfter(Player player, Player hitter) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            final Set<LastHitter> hitters = this.lastHitters.get(player);

            if (hitters != null) {
                final LastHitter lastHitter = this.getLastHitter(player, hitter);

                if (lastHitter != null) {
                    hitters.remove(lastHitter);

                    if (hitters.size() == 0) {
                        this.lastHitters.remove(player);
                    } else {
                        this.lastHitters.put(player, hitters);
                    }
                }
            }
        }, this.removeTime);
    }

    public LastHitter getLastHitter(Player player, Player hitter) {
        final Set<LastHitter> lastHitters = this.lastHitters.get(player);

        if (lastHitters != null) {
            return lastHitters.stream().filter(h -> h.asPlayer().equals(hitter)).findFirst().orElse(null);
        }
        return null;
    }

    public List<LastHitter> getLastHitters(Player player) {
        final Set<LastHitter> lastHitters = this.lastHitters.get(player);

        if (lastHitters != null) {
            return lastHitters.stream().sorted(Comparator.comparing(LastHitter::getHits)).collect(Collectors.toList());
        }
        return null;
    }

    public void removeLastHitters(Player player) {
        this.lastHitters.remove(player);
    }

    public Map<Player, Set<LastHitter>> getLastHittersMap() {
        return this.lastHitters;
    }

    public long getRemoveTime() {
        return this.removeTime;
    }

    public void setRemoveTime(long removeTime) {
        this.removeTime = removeTime;
    }

    public class LastHitter {

        private final Player player;
        private int hits;

        public LastHitter(Player player) {
            this.player = player;
            this.hits = 1;
        }

        public Player asPlayer() {
            return this.player;
        }

        public HyriGamePlayer asGamePlayer() {
            return hyrame.getGameManager().getCurrentGame().getPlayer(this.player.getUniqueId());
        }

        public Integer getHits() {
            return this.hits;
        }

        public void addHit() {
            this.hits++;
        }

    }

}
