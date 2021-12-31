package fr.hyriode.hyrame.impl.tab;

import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.hyriapi.player.IHyriPlayer;
import fr.hyriode.hyriapi.rank.EHyriRank;
import fr.hyriode.hyriapi.rank.HyriRank;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import fr.hyriode.hyrame.scoreboard.team.ScoreboardTeam;
import fr.hyriode.hyrame.scoreboard.team.ScoreboardTeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:50
 */
public class HyriTabHandler {

    private final ScoreboardTeamHandler teamHandler;

    private final HyriLanguage language;
    private final Hyrame hyrame;

    public HyriTabHandler(Hyrame hyrame, HyriLanguage language) {
        this.hyrame = hyrame;
        this.language = language;
        this.teamHandler = new ScoreboardTeamHandler();

        for (EHyriRank rankEnum : EHyriRank.values()) {
            final HyriRank rank = HyriAPI.get().getRankManager().getRank(rankEnum);
            final ScoreboardTeam team = new ScoreboardTeam(rank.getName(), this.getAlphabetLetter(rankEnum.getId()), "", "", "");

            if (rank.getType().equals(EHyriRank.PLAYER)) {
                final String display = ChatColor.GRAY + "";

                team.setDisplay(display);
                team.setPrefix(display);
            } else {
                final String formattedDisplay = this.getFormattedDisplayName(this.getDisplayName(rank));

                team.setDisplay(formattedDisplay);
                team.setPrefix(formattedDisplay);
            }

            this.teamHandler.addTeam(team);
        }
    }

    public void onLogin(Player player) {
        if (this.hyrame.getConfiguration().areRanksInTabList()) {
            if (this.hasSameLanguage(player)) {
                this.teamHandler.addReceiver(player);
            }

            final IHyriPlayer hyriPlayer = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());
            final HyriRank rank = hyriPlayer.getRank();
            final ScoreboardTeam team = this.teamHandler.getTeamByName(rank.getName());

            this.teamHandler.addPlayerToTeam(player, team);
        }
    }

    public void onLogout(Player player) {
        if (this.hasSameLanguage(player)) {
            this.teamHandler.removeReceiver(player);
        }
    }

    public void enable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.hasSameLanguage(player)) {
                this.teamHandler.addReceiver(player);
            }
        }
    }

    public void disable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.onLogout(player);
        }
    }

    private boolean hasSameLanguage(Player player) {
        return HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId()).getSettings().getLanguage() == this.language;
    }

    private String getAlphabetLetter(int id) {
        return String.valueOf("abcdefghijklmnopqrstuvwxyz".toCharArray()[id % 24]);
    }

    private String getDisplayName(HyriRank rank) {
        return rank.getDisplayNames().get(this.language) != null ? rank.getDisplayNames().get(this.language) : rank.getDisplayNames().get(HyriLanguage.EN);
    }

    private String getFormattedDisplayName(String displayName) {
        displayName += ChatColor.WHITE + "・" + displayName.substring(0, 2);

        return displayName.substring(0, Math.min(displayName.length(), 16));
    }

}
