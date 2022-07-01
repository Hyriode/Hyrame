package fr.hyriode.hyrame.impl.tablist;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.nickname.IHyriNickname;
import fr.hyriode.api.rank.HyriRank;
import fr.hyriode.api.rank.type.HyriPlayerRankType;
import fr.hyriode.api.rank.type.HyriStaffRankType;
import fr.hyriode.api.rank.type.IHyriRankType;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.scoreboard.team.HyriScoreboardTeam;
import fr.hyriode.hyrame.tablist.ITabListManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by AstFaster
 * on 30/05/2022 at 20:45
 */
public class HyriRanksHandler {

    private final IHyrame hyrame;
    private final ITabListManager tabListManager;

    public HyriRanksHandler(IHyrame hyrame, ITabListManager tabListManager) {
        this.hyrame = hyrame;
        this.tabListManager = tabListManager;

        this.loadDefaults(HyriPlayerRankType.values());
        this.loadDefaults(HyriStaffRankType.values());
    }

    private void loadDefaults(IHyriRankType[] rankTypes) {
        for (IHyriRankType rankType : rankTypes) {
            final String display = rankType.withSeparator() ? rankType.getDefaultPrefix() + HyriRank.SEPARATOR + rankType.getDefaultColor() : rankType.getDefaultColor() + "";
            final HyriScoreboardTeam team = new HyriScoreboardTeam(rankType.getName(), this.getAlphabetLetter(rankType.getTabListPriority()), display, display, "");

            this.tabListManager.registerTeam(team);
        }
    }

    public void onLogin(Player player) {
        if (this.hyrame.getConfiguration().areRanksInTabList()) {
            final String playerName = player.getName();
            final String teamPlayerName = (playerName.length() >= 15 ? playerName.substring(0, 15) : playerName);
            final UUID playerId = player.getUniqueId();
            final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(playerId);
            final IHyriNickname nickname = account.getNickname();
            final HyriRank rank = account.getRank();

            final HyriScoreboardTeam team;
            if (nickname != null) {
                team = this.tabListManager.getTeam(nickname.getRank().getName());
            } else if (account.hasHyriPlus() && !account.getRank().isStaff()) {
                final HyriScoreboardTeam oldTeam = this.tabListManager.getTeam(playerId.toString());

                if (oldTeam == null) {
                    final String display = rank.withSeparator() ? ChatColor.translateAlternateColorCodes('&', account.getPrefix()) + HyriRank.SEPARATOR + rank.getMainColor() : rank.getMainColor().toString();

                    team = new HyriScoreboardTeam(playerId.toString(), this.getAlphabetLetter(account.getTabListPriority()) + teamPlayerName, display, display, "");

                    this.tabListManager.registerTeam(team);
                } else {
                    team = oldTeam;
                }
            } else if (!rank.hasCustomPrefix()) {
                team = this.tabListManager.getTeam(rank.getType().getName());
            } else {
                final HyriScoreboardTeam oldTeam = this.tabListManager.getTeam(playerId.toString());

                if (oldTeam == null) {
                    final String prefix = account.getPrefix();
                    final String display = ChatColor.translateAlternateColorCodes('&', prefix) + HyriRank.SEPARATOR + rank.getMainColor();

                    team = new HyriScoreboardTeam(playerId.toString(), this.getAlphabetLetter(account.getTabListPriority()) + teamPlayerName, display, display, "");

                    this.tabListManager.registerTeam(team);
                } else {
                    team = oldTeam;
                }
            }

            this.tabListManager.addPlayerInTeam(player, team.getName());
        }
    }

    public void onLogout(Player player) {
        this.tabListManager.unregisterTeam(player.getUniqueId().toString());
    }

    private String getAlphabetLetter(int id) {
        return String.valueOf("abcdefghijklmnopqrstuvwxyz".toCharArray()[id % 24]);
    }

}
