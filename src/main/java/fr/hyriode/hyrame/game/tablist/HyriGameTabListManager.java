package fr.hyriode.hyrame.game.tablist;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.scoreboard.team.HyriScoreboardTeam;
import fr.hyriode.hyrame.tablist.ITabListManager;
import fr.hyriode.hyrame.utils.Symbols;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 17/09/2021 at 18:45
 */
public class HyriGameTabListManager {

    private static final String DEFAULT = ".default";

    private final HyriGame<?> game;
    private final ITabListManager tabListManager;

    public HyriGameTabListManager(HyriGame<?> game, ITabListManager tabListManager) {
        this.game = game;
        this.tabListManager = tabListManager;

        this.addTeams();
    }

    private void addTeams() {
        this.game.getTeams().forEach(this::addTeam);

        this.tabListManager.registerTeam(new HyriScoreboardTeam(DEFAULT, DEFAULT, ChatColor.GRAY + Symbols.CROSS + " ", ChatColor.GRAY + Symbols.CROSS + " ", ""));
    }

    public void addTeam(HyriGameTeam team) {
        this.tabListManager.registerTeam(new HyriScoreboardTeam(team.getName(), team.getName(), team.getColor().getChatColor() + "", team.getColor().getChatColor() + "",  "", team.getNameTagVisibility()));
    }

    public void updateTeam(HyriGameTeam team) {
        final String teamName = team.getName();
        final HyriScoreboardTeam scoreboardTeam = this.tabListManager.getTeam(teamName);
        final String display = String.valueOf(team.getColor().getChatColor());

        scoreboardTeam.setDisplay(display);
        scoreboardTeam.setPrefix(display);
        scoreboardTeam.setNameTagVisibility(team.getNameTagVisibility());

        this.tabListManager.updateTeam(teamName);
    }

    public void removeTeam(HyriGameTeam team) {
        this.tabListManager.unregisterTeam(team.getName());
    }

    public void handleLogin(Player player) {
        this.tabListManager.addPlayerInTeam(player, DEFAULT);
    }

    public void handleReconnection(HyriGamePlayer gamePlayer) {
        final Player player = gamePlayer.getPlayer();
        final HyriGameTeam team = gamePlayer.getTeam();

        if (team == null) {
            return;
        }

        this.tabListManager.addPlayerInTeam(player, team.getName());
    }

    public void updatePlayer(HyriGamePlayer gamePlayer) {
        final HyriGameTeam team = gamePlayer.getTeam();

        this.tabListManager.addPlayerInTeam(gamePlayer.getPlayer(), team == null ? DEFAULT : team.getName());
    }

    public void updateTabList() {
        for (HyriGamePlayer player : this.game.getPlayers()) {
           this.updatePlayer(player);
        }
    }

}
