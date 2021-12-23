package fr.hyriode.hyrame.util;

import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyriapi.rank.EHyriRank;
import fr.hyriode.hyriapi.rank.HyriRank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
     * @return The displayed rank prefix
     */
    public static String formatRankForPlayer(HyriRank rank, Player target) {
        final HyriLanguageMessage rankDisplayNames = HyriLanguageMessage.from(rank.getDisplayNames());
        final String value = rankDisplayNames.getForPlayer(target);

        if (rank.getType() == EHyriRank.PLAYER) {
            return ChatColor.GRAY + "";
        }

        return value + ChatColor.WHITE + "・" + value.substring(0, 2);
    }

}
