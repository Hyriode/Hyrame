package fr.hyriode.hyrame.impl.tab;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.rank.EHyriRank;
import fr.hyriode.api.rank.HyriRank;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.scoreboard.team.HyriScoreboardTeam;
import fr.hyriode.hyrame.scoreboard.team.HyriScoreboardTeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:50
 */
public class HyriTabHandler {

    private final HyriScoreboardTeamHandler teamHandler;

    private final Hyrame hyrame;

    public HyriTabHandler(Hyrame hyrame) {
        this.hyrame = hyrame;
        this.teamHandler = new HyriScoreboardTeamHandler();

        for (EHyriRank rankEnum : EHyriRank.values()) {
            final HyriRank rank = HyriAPI.get().getRankManager().getRank(rankEnum);
            final HyriScoreboardTeam team = new HyriScoreboardTeam(rank.getName(), this.getAlphabetLetter(rankEnum.getId()), "", "", "");

            if (rank.getType().equals(EHyriRank.PLAYER)) {
                final String display = ChatColor.GRAY + "";

                team.setDisplay(display);
                team.setPrefix(display);
            } else {
                final String formattedDisplay = this.getFormattedDisplayName(rank.getPrefix());

                team.setDisplay(formattedDisplay);
                team.setPrefix(formattedDisplay);
            }

            this.teamHandler.addTeam(team);
        }
    }

    public void onLogin(Player player) {
        if (this.hyrame.getConfiguration().areRanksInTabList()) {
            this.teamHandler.addReceiver(player);

            final IHyriPlayer hyriPlayer = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());
            final HyriRank rank = hyriPlayer.getRank();
            final HyriScoreboardTeam team = this.teamHandler.getTeamByName(rank.getName());

            this.teamHandler.addPlayerToTeam(player, team);
        }
    }

    public void onLogout(Player player) {
        this.teamHandler.removeReceiver(player);
    }

    public void enable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.teamHandler.addReceiver(player);
        }
    }

    public void disable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.onLogout(player);
        }
    }

    private String getAlphabetLetter(int id) {
        return String.valueOf("abcdefghijklmnopqrstuvwxyz".toCharArray()[id % 24]);
    }

    private String getFormattedDisplayName(String displayName) {
        displayName += ChatColor.WHITE + "・" + displayName.substring(0, 2);

        return displayName.substring(0, Math.min(displayName.length(), 16));
    }

}
