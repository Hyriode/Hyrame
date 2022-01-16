package fr.hyriode.hyrame.game.tab;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.scoreboard.team.ScoreboardTeam;
import fr.hyriode.hyrame.scoreboard.team.ScoreboardTeamHandler;
import fr.hyriode.hyrame.utils.Symbols;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 17/09/2021 at 18:45
 */
public class HyriGameTabListManager {

    private static final String DEFAULT = "default";

    private final ScoreboardTeamHandler teamHandler;

    private final HyriGame<?> game;

    public HyriGameTabListManager(HyriGame<?> game) {
        this.game = game;
        this.teamHandler = new ScoreboardTeamHandler();

        this.addTeams();
    }

    private void addTeams() {
        this.game.getTeams().forEach(this::addTeam);
        this.teamHandler.addTeam(new ScoreboardTeam(DEFAULT, DEFAULT, ChatColor.GRAY + Symbols.CROSS + " ", ChatColor.GRAY + Symbols.CROSS + " ", ""));
    }

    public void addTeam(HyriGameTeam team) {
        this.teamHandler.addTeam(new ScoreboardTeam(team.getName(), team.getName(), team.getColor().getChatColor() + "", team.getColor().getChatColor() + "", ""));
    }

    public void handleLogin(Player player) {
        this.teamHandler.addReceiver(player);

        this.teamHandler.addPlayerToTeam(player, this.teamHandler.getTeamByName(DEFAULT));
    }

    public void handleLogout(Player player) {
        this.teamHandler.removeReceiver(player);
    }

    public void updateTabList() {
        for (HyriGamePlayer player : this.game.getPlayers()) {
            this.teamHandler.addPlayerToTeam(player.getPlayer(), this.teamHandler.getTeamByName(DEFAULT));
        }

        for (HyriGameTeam team : this.game.getTeams()) {
            for (HyriGamePlayer player : team.getPlayers()) {
                final ScoreboardTeam scoreboardTeam = this.teamHandler.getTeamByName(team.getName());

                if (scoreboardTeam != null) {
                    this.teamHandler.addPlayerToTeam(player.getPlayer(), scoreboardTeam);
                }
            }
        }
    }

}
