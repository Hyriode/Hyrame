package fr.hyriode.hyrame.impl.module.friend;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.event.PlayerJoinNetworkEvent;
import fr.hyriode.api.player.event.PlayerQuitNetworkEvent;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 29/06/2022 at 17:02
 */
public class FriendListener {

    @HyriEventHandler
    public void onNetworkJoin(PlayerJoinNetworkEvent event) {
        this.checkNotification(event.getPlayer(), HyriLanguageMessage.get("message.friend.joined"));
    }

    @HyriEventHandler
    public void onNetworkQuit(PlayerQuitNetworkEvent event) {
        this.checkNotification(event.getPlayer(), HyriLanguageMessage.get("message.friend.left"));
    }

    private void checkNotification(IHyriPlayer account, HyriLanguageMessage message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            HyriAPI.get().getFriendManager().createHandlerAsync(player.getUniqueId()).whenComplete((handler, throwable) -> {
                if (handler.areFriends(account.getUniqueId())) {
                    final IHyriPlayer target = IHyriPlayer.get(player.getUniqueId());

                    if (target.getSettings().isFriendConnectionNotificationEnabled()) {
                        player.sendMessage(message.getForPlayer(target).replace("%player%", ChatColor.LIGHT_PURPLE + account.getName()));
                    }
                }
            });
        }
    }

}
