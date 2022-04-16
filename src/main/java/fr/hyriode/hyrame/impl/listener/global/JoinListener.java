package fr.hyriode.hyrame.impl.listener.global;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyrame.utils.BroadcastUtil;
import fr.hyriode.hyrame.utils.RankUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 16:11
 */
public class JoinListener extends HyriListener<HyramePlugin> {

    public JoinListener(HyramePlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final IHyriPlayer account =  HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());
        final IHyrame hyrame = this.plugin.getHyrame();
        final HyriGame<?> game = hyrame.getGameManager().getCurrentGame();

        event.setJoinMessage("");

        if (game != null) {
            final HyriGameState state = game.getState();

            if (state == HyriGameState.WAITING || state == HyriGameState.READY) {
                final String playerCounter = (game.canStart() ? ChatColor.GREEN : ChatColor.RED) + " (" + game.getPlayers().size() + "/" + game.getMaxPlayers() + ")";

                BroadcastUtil.broadcast(target -> RankUtil.formatRankForPlayer(account.getRank(), target) + player.getDisplayName() + ChatColor.GRAY + hyrame.getLanguageManager().getValue(target, "message.game-join") + playerCounter);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final IHyriPlayer account =  HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());
        final IHyrame hyrame = this.plugin.getHyrame();
        final HyriGame<?> game = hyrame.getGameManager().getCurrentGame();

        event.setQuitMessage("");

        if (game != null) {
            final HyriGameState state = game.getState();

            if (state == HyriGameState.WAITING || state == HyriGameState.READY) {
                final String playerCounter = (game.canStart() ? ChatColor.GREEN : ChatColor.RED) + " (" + game.getPlayers().size() + "/" + game.getMaxPlayers() + ")";

                BroadcastUtil.broadcast(target -> RankUtil.formatRankForPlayer(account.getRank(), target) + player.getDisplayName() + ChatColor.GRAY + hyrame.getLanguageManager().getValue(target, "message.game-left") + playerCounter);
            }
        }
    }

    @EventHandler
    public void onPlayerKicked(PlayerKickEvent event) {
        event.setLeaveMessage("");
    }

}
