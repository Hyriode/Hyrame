package fr.hyriode.hyrame.game.protocol;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.event.player.HyriGameLeaveEvent;
import fr.hyriode.hyrame.game.event.player.HyriGamePlayerEvent;
import fr.hyriode.hyrame.game.scoreboard.HyriWaitingScoreboard;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.scoreboard.HyriScoreboard;
import fr.hyriode.hyrame.scoreboard.IHyriScoreboardManager;
import fr.hyriode.hyrame.utils.BroadcastUtil;
import fr.hyriode.hyrame.utils.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 24/02/2022 at 19:53
 */
public class HyriWaitingProtocol extends HyriGameProtocol implements Listener {

    private static final Class<HyriWaitingScoreboard> SCOREBOARD_CLASS = HyriWaitingScoreboard.class;

    private boolean teamSelector;

    private final JavaPlugin plugin;

    public HyriWaitingProtocol(IHyrame hyrame, JavaPlugin plugin, boolean teamSelector) {
        super(hyrame, "waiting");
        this.plugin = plugin;
        this.teamSelector = teamSelector;

        HyriAPI.get().getEventBus().register(this);
    }

    public HyriWaitingProtocol(IHyrame hyrame, JavaPlugin plugin) {
        this(hyrame, plugin, true);
    }

    public HyriWaitingProtocol withTeamSelector(boolean teamSelector) {
        this.teamSelector = teamSelector;
        return this;
    }

    @Override
    void enable() {
        final HyriGame<?> game = this.getGame();

        game.getStartingTimer().setOnTimeChanged(time -> game.getPlayers().forEach(gamePlayer -> this.runActionOnScoreboard(gamePlayer.getPlayer(), scoreboard -> {
            scoreboard.setTime(time);
            scoreboard.update();
        })));
    }

    @Override
    void disable() {
        final HyriGame<?> game = this.getGame();

        game.getPlayers().forEach(player -> player.getPlayer().setCanPickupItems(true));
        game.getStartingTimer().setOnTimeChanged(null);

        this.hyrame.getScoreboardManager().getScoreboards(SCOREBOARD_CLASS).forEach(HyriScoreboard::hide);
    }

    @HyriEventHandler
    public void onJoin(HyriGamePlayerEvent event) {
        final Player player = event.getGamePlayer().getPlayer();
        final UUID playerId = player.getUniqueId();
        final HyriGame<?> game = this.getGame();

        if (game.getPlayer(playerId) == null) {
            return;
        }

        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(playerId);

        PlayerUtil.resetPlayer(player, true);

        player.setCanPickupItems(false);

        if (this.teamSelector) {
            HyriGameItems.TEAM_SELECTOR.give(this.hyrame, player, 0);
        }

        HyriGameItems.LEAVE.give(this.hyrame, player, 8);

        new HyriWaitingScoreboard(game, this.plugin, player).show();

        this.updateScoreboards();

        final String playerCounter = (game.canStart() ? ChatColor.GREEN : ChatColor.RED) + " (" + game.getPlayers().size() + "/" + game.getMaxPlayers() + ")";

        BroadcastUtil.broadcast(target -> account.getNameWithRank(true) + ChatColor.GRAY + hyrame.getLanguageManager().getValue(target, "message.game-join") + playerCounter);
    }

    @HyriEventHandler
    public void onLeave(HyriGameLeaveEvent event) {
        final Player player = event.getGamePlayer().getPlayer();
        final HyriGame<?> game = this.getGame();

        if (game.getPlayer(player.getUniqueId()) == null) {
            return;
        }

        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());

        this.updateScoreboards();

        final String playerCounter = (game.canStart() ? ChatColor.GREEN : ChatColor.RED) + " (" + game.getPlayers().size() + "/" + game.getMaxPlayers() + ")";

        BroadcastUtil.broadcast(target -> account.getNameWithRank(true) + ChatColor.GRAY + hyrame.getLanguageManager().getValue(target, "message.game-left") + playerCounter);
    }

    private void updateScoreboards() {
        this.hyrame.getScoreboardManager().getScoreboards(SCOREBOARD_CLASS).forEach(HyriWaitingScoreboard::update);
    }

    private void runActionOnScoreboard(Player player, Consumer<HyriWaitingScoreboard> action) {
        final IHyriScoreboardManager scoreboardManager = this.hyrame.getScoreboardManager();

        if (scoreboardManager.isPlayerScoreboard(player, SCOREBOARD_CLASS)) {
            action.accept((HyriWaitingScoreboard) scoreboardManager.getPlayerScoreboard(player));
        }
    }

}
