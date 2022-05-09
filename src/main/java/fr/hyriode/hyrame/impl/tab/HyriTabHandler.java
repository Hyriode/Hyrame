package fr.hyriode.hyrame.impl.tab;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.nickname.IHyriNickname;
import fr.hyriode.api.rank.HyriRank;
import fr.hyriode.api.rank.type.HyriPlayerRankType;
import fr.hyriode.api.rank.type.HyriStaffRankType;
import fr.hyriode.api.rank.type.IHyriRankType;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.scoreboard.team.HyriScoreboardTeam;
import fr.hyriode.hyrame.scoreboard.team.HyriScoreboardTeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

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

        this.loadDefaults(HyriPlayerRankType.values());
        this.loadDefaults(HyriStaffRankType.values());
    }

    private void loadDefaults(IHyriRankType[] rankTypes) {
        for (IHyriRankType rankType : rankTypes) {
            final HyriScoreboardTeam team = new HyriScoreboardTeam(rankType.getName(), this.getAlphabetLetter(rankType.getTabListPriority()), "", "", "");

            final String display;
            if (rankType.withSeparator()) {
                display = rankType.getDefaultPrefix() + HyriRank.SEPARATOR + rankType.getDefaultColor();
            } else {
                display = rankType.getDefaultColor() + "";
            }

            team.setDisplay(display);
            team.setPrefix(display);

            this.teamHandler.addTeam(team);
        }
    }

    public void onLogin(Player player) {
        if (this.hyrame.getConfiguration().areRanksInTabList()) {
            final UUID playerId = player.getUniqueId();
            final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(playerId);
            final IHyriNickname nickname = account.getNickname();
            final HyriRank rank = account.getRank();
            final IHyriRankType rankType =  rank.getType();

            this.teamHandler.addReceiver(player);

            final HyriScoreboardTeam team;
            if (nickname != null) {
                team = this.teamHandler.getTeamByName(nickname.getRank().getName());
            } else if (account.hasHyriPlus() && !account.getRank().isStaff()) {
                final HyriScoreboardTeam oldTeam = this.teamHandler.getTeamByName(playerId.toString());

                if (oldTeam == null) {
                    team = new HyriScoreboardTeam(playerId.toString(), this.getAlphabetLetter(8) + player.getName(), "", "", "");

                    final String display;
                    if (rank.withSeparator()) {
                        display = ChatColor.translateAlternateColorCodes('&', account.getPrefix()) + HyriRank.SEPARATOR + rank.getMainColor();
                    } else {
                        display = rank.getMainColor().toString();
                    }

                    team.setDisplay(display);
                    team.setPrefix(display);

                    this.teamHandler.addTeam(team);
                } else {
                    team = oldTeam;
                }
            } else if (!rank.hasCustomPrefix()) {
                team = this.teamHandler.getTeamByName(rank.getType().getName());
            } else {
                final HyriScoreboardTeam oldTeam = this.teamHandler.getTeamByName(playerId.toString());

                if (oldTeam == null) {
                    final String prefix = account.getPrefix();
                    final String display = ChatColor.translateAlternateColorCodes('&', prefix) + HyriRank.SEPARATOR + rank.getMainColor();

                    team = new HyriScoreboardTeam(playerId.toString(), this.getAlphabetLetter(rankType.getPriority()) + player.getName(), "", "", "");

                    team.setDisplay(display);
                    team.setPrefix(display);

                    this.teamHandler.addTeam(team);
                } else {
                    team = oldTeam;
                }
            }

            this.teamHandler.addPlayerToTeam(player, team);
        }
    }

    public void onLogout(Player player) {
        this.teamHandler.removeReceiver(player);

        this.teamHandler.removeTeam(player.getUniqueId().toString());
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

}
