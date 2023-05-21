package fr.hyriode.hyrame.impl.scoreboard;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.scoreboard.HyriScoreboard;
import fr.hyriode.hyrame.scoreboard.HyriScoreboardEvent;
import fr.hyriode.hyrame.scoreboard.IScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 25/02/2022 at 18:46
 */
public class ScoreboardManager implements IScoreboardManager, Listener {

    private final Map<UUID, HyriScoreboard> playersScoreboards = new ConcurrentHashMap<>();

    public ScoreboardManager(IHyrame hyrame) {
        HyriAPI.get().getEventBus().register(this);

        final Server server = hyrame.getPlugin().getServer();

        server.getPluginManager().registerEvents(this, hyrame.getPlugin());
        server.getScheduler().runTaskTimer(hyrame.getPlugin(), () -> {
            for (HyriScoreboard scoreboard : this.playersScoreboards.values()) {
                scoreboard.onTick();
            }
        }, 1L, 1L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final HyriScoreboard scoreboard = this.playersScoreboards.remove(event.getPlayer().getUniqueId());

        if (scoreboard != null) {
            scoreboard.hide();
        }
    }

    @HyriEventHandler
    public void onScoreboardEvent(HyriScoreboardEvent event) {
        final Player player = event.getPlayer();
        final HyriScoreboard scoreboard = event.getScoreboard();
        final HyriScoreboardEvent.Action action = event.getAction();

        if (action == HyriScoreboardEvent.Action.SHOW) {
            this.playersScoreboards.put(player.getUniqueId(), scoreboard);
        } else if (action == HyriScoreboardEvent.Action.HIDE) {
            this.playersScoreboards.remove(player.getUniqueId());
        }
    }

    @Override
    public boolean removeScoreboardFromPlayer(Player player) {
        final HyriScoreboard scoreboard = this.playersScoreboards.get(player.getUniqueId());

        if (scoreboard != null) {
            scoreboard.hide();
            return true;
        }
        return false;
    }

    @Override
    public void removeScoreboardFromPlayers(Class<? extends HyriScoreboard> scoreboardClass) {
        for (Map.Entry<UUID, HyriScoreboard> entry : this.playersScoreboards.entrySet()) {
            if (entry.getValue().getClass() == scoreboardClass) {
                this.removeScoreboardFromPlayer(Bukkit.getPlayer(entry.getKey()));
            }
        }
    }

    @Override
    public HyriScoreboard getPlayerScoreboard(Player player) {
        return this.playersScoreboards.get(player.getUniqueId());
    }

    @Override
    public boolean isPlayerScoreboard(Player player, Class<? extends HyriScoreboard> scoreboardClass) {
        final HyriScoreboard scoreboard = this.getPlayerScoreboard(player);

        return scoreboard != null && scoreboard.getClass() == scoreboardClass;
    }

    @Override
    public Map<UUID, HyriScoreboard> getPlayersScoreboards() {
        return this.playersScoreboards;
    }

    @Override
    public <T extends HyriScoreboard> Map<T, UUID> getScoreboardsMap(Class<T> scoreboardClass) {
        final Map<T, UUID> scoreboards = new HashMap<>();

        for (Map.Entry<UUID, HyriScoreboard> entry : this.playersScoreboards.entrySet()) {
            final HyriScoreboard scoreboard = entry.getValue();

            if (scoreboard.getClass() == scoreboardClass) {
                scoreboards.put(scoreboardClass.cast(scoreboard), entry.getKey());
            }
        }
        return scoreboards;
    }

    @Override
    public <T extends HyriScoreboard> List<T> getScoreboards(Class<T> scoreboardClass) {
        final List<T> scoreboards = new ArrayList<>();

        for (Map.Entry<UUID, HyriScoreboard> entry : this.playersScoreboards.entrySet()) {
            final HyriScoreboard scoreboard = entry.getValue();

            if (scoreboard.getClass() == scoreboardClass) {
                scoreboards.add(scoreboardClass.cast(scoreboard));
            }
        }
        return scoreboards;
    }

}
