package fr.hyriode.hyrame.impl.listener.model.global;

import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.hyriapi.player.IHyriPlayer;
import fr.hyriode.hyriapi.player.IHyriPlayerManager;
import fr.hyriode.hyriapi.rank.EHyriRank;
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
public class GlobalJoinHandler extends HyriListener<HyramePlugin> {

    public GlobalJoinHandler(HyramePlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        final IHyriPlayerManager playerManager = HyriAPI.get().getPlayerManager();
        final UUID uuid = event.getUniqueId();

        IHyriPlayer player = playerManager.getPlayer(uuid);
        if (player == null) {
            player = playerManager.createPlayer(uuid, event.getName());
        }

        if (player == null) {
            event.setKickMessage("An error occurred when loading your profile!");
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final IHyriPlayerManager playerManager = HyriAPI.get().getPlayerManager();
        final IHyriPlayer hyriPlayer = playerManager.getPlayer(player.getUniqueId());

        if (hyriPlayer.getRank().getName().equals(HyriAPI.get().getRankManager().getRank(EHyriRank.ADMINISTRATOR).getName())) {
            player.setOp(true);
        }

        hyriPlayer.setLastLoginDate(new Date(System.currentTimeMillis()));

        playerManager.sendPlayer(hyriPlayer);

        event.setJoinMessage("");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLeave(PlayerQuitEvent event) {
        final IHyriPlayerManager playerManager = HyriAPI.get().getPlayerManager();
        final IHyriPlayer hyriPlayer = playerManager.getPlayer(event.getPlayer().getUniqueId());

        event.setQuitMessage("");

        hyriPlayer.setPlayTime(hyriPlayer.getPlayTime() + (System.currentTimeMillis() - hyriPlayer.getLastLoginDate().getTime()));
        playerManager.sendPlayer(hyriPlayer);
    }

    @EventHandler
    public void onPlayerKicked(PlayerKickEvent event) {
        event.setLeaveMessage("");
    }

}
