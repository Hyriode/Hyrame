package fr.hyriode.hyrame.game.protocol;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.scoreboard.HyriWaitingScoreboard;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.scoreboard.HyriScoreboard;
import fr.hyriode.hyrame.scoreboard.IHyriScoreboardManager;
import fr.hyriode.hyrame.utils.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

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

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final HyriGame<?> game = this.getGame();

        PlayerUtil.resetPlayer(player, true);

        player.setCanPickupItems(false);

        if (this.teamSelector) {
            HyriGameItems.TEAM_SELECTOR.give(this.hyrame, player, 0);
        }

        HyriGameItems.LEAVE.give(this.hyrame, player, 8);

        new HyriWaitingScoreboard(game, this.plugin, player).show();

        this.updateScoreboards();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.updateScoreboards();
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
