package fr.hyriode.hyrame.impl.chat;

import fr.hyriode.hyrame.chat.IHyriChatHandler;
import fr.hyriode.hyrame.utils.RankUtil;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.hyriapi.player.IHyriPlayer;
import fr.hyriode.hyriapi.rank.EHyriRank;
import fr.hyriode.hyriapi.rank.HyriRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 14/12/2021 at 19:23
 */
public class HyriDefaultChatHandler implements IHyriChatHandler {

    private boolean cancelled;

    @Override
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        if (!this.cancelled) {
            final Player player = event.getPlayer();
            final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());
            final HyriRank rank = account.getRank();

            for (Player target : Bukkit.getOnlinePlayers()) {
                ChatColor color;
                if (rank.getType() == EHyriRank.PLAYER) {
                    color = ChatColor.GRAY;
                } else {
                    color = ChatColor.WHITE;
                }

                target.sendMessage(String.format(this.format(), RankUtil.formatRankForPlayer(rank, target) + player.getDisplayName(), color + event.getMessage()));
            }
        }
    }

    @Override
    public String format() {
        return "%s" + ChatColor.WHITE + ": %s";
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
