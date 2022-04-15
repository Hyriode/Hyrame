package fr.hyriode.hyrame.utils;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.rank.EHyriRank;
import fr.hyriode.api.rank.HyriRank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 18/12/2021 at 08:35
 */
public class RankUtil {

    /**
     * Format player rank<br>
     * Useful to display as the same of the tab list and the chat
     *
     * @param rank Player rank
     * @param target Target to send the formatted display (to handle languages)
     * @param separator Add a separator or not after the rank name
     * @return The displayed rank prefix
     */
    public static String formatRankForPlayer(HyriRank rank, Player target, boolean separator) {
        if (rank.getType() == EHyriRank.PLAYER) {
            return ChatColor.GRAY + "";
        }

        return rank.getPrefix() + (separator ? ChatColor.WHITE + "・" + rank.getPrefix().substring(0, 2) : "");
    }

    /**
     * Format player rank<br>
     * Useful to display as the same of the tab list and the chat
     *
     * @param rank Player rank
     * @param target Target to send the formatted display (to handle languages)
     * @return The displayed rank prefix
     */
    public static String formatRankForPlayer(HyriRank rank, Player target) {
        return formatRankForPlayer(rank, target, true);
    }

    /**
     * Format the name of a player with his rank in the good language
     *
     * @param player The player that has the rank
     * @param target The player that will have the rank traduced
     * @return The formatted player name
     */
    public static String formatPlayerName(UUID player, Player target) {
        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(player);
        final HyriRank rank = account.getRank();

        return formatRankForPlayer(rank, target) + account.getName();
    }

}
