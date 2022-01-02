package fr.hyriode.hyrame.impl.listener.model.global;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyrame.utils.BroadcastUtil;
import fr.hyriode.hyrame.utils.RankUtil;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.hyriapi.player.IHyriPlayer;
import fr.hyriode.hyriapi.player.IHyriPlayerManager;
import fr.hyriode.hyriapi.rank.EHyriRank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Date;
import java.util.UUID;

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
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        try {
            final IHyriPlayerManager playerManager = HyriAPI.get().getPlayerManager();
            final UUID uuid = event.getUniqueId();

            IHyriPlayer player = playerManager.getPlayer(uuid);
            if (player == null) {
                player = playerManager.createPlayer(uuid, event.getName());
            }

            if (player != null) {
                return;
            }
        } catch (Exception ignored) {}

        event.setKickMessage("An error occurred when loading your profile!");
        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final IHyrame hyrame = this.plugin.getHyrame();
        final IHyriPlayerManager playerManager = HyriAPI.get().getPlayerManager();
        final IHyriPlayer account = playerManager.getPlayer(player.getUniqueId());

        if (account.getRank().getType().equals(EHyriRank.ADMINISTRATOR)) {
            player.setOp(true);
        }

        playerManager.removePlayerId(account.getName());

        account.setName(player.getName());
        account.setLastLoginDate(new Date(System.currentTimeMillis()));

        playerManager.sendPlayer(account);
        playerManager.setPlayerId(account.getName(), account.getUUID());

        final HyriGame<?> game = hyrame.getGameManager().getCurrentGame();

        event.setJoinMessage("");

        if (game != null) {
            final String playerCounter = (game.canStart() ? ChatColor.GREEN : ChatColor.RED) + " (" + game.getPlayers().size() + "/" + game.getMaxPlayers() + ")";

            BroadcastUtil.broadcast(target -> RankUtil.formatRankForPlayer(account.getRank(), target) + player.getDisplayName() + ChatColor.GRAY + hyrame.getLanguageManager().getValue(target, "message.game-join") + playerCounter);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final IHyriPlayerManager playerManager = HyriAPI.get().getPlayerManager();
        final IHyriPlayer account = playerManager.getPlayer(player.getUniqueId());
        final IHyrame hyrame = this.plugin.getHyrame();
        final HyriGame<?> game = hyrame.getGameManager().getCurrentGame();

        account.setPlayTime(account.getPlayTime() + (System.currentTimeMillis() - account.getLastLoginDate().getTime()));

        playerManager.sendPlayer(account);

        event.setQuitMessage("");

        if (game != null) {
            final String playerCounter = (game.canStart() ? ChatColor.GREEN : ChatColor.RED) + " (" + game.getPlayers().size() + "/" + game.getMaxPlayers() + ")";

            BroadcastUtil.broadcast(target -> RankUtil.formatRankForPlayer(account.getRank(), target) + player.getDisplayName() + ChatColor.GRAY + hyrame.getLanguageManager().getValue(target, "message.game-left") + playerCounter);
        }
    }

    @EventHandler
    public void onPlayerKicked(PlayerKickEvent event) {
        event.setLeaveMessage("");
    }

}
