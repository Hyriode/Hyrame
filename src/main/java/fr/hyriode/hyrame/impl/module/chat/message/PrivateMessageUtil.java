package fr.hyriode.hyrame.impl.module.chat.message;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.settings.HyriPrivateMessagesLevel;
import fr.hyriode.api.settings.IHyriPlayerSettings;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.utils.PlayerUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 15:35
 */
class PrivateMessageUtil {

    public static void replyToMessage(Player player, String message) {
        final IHyriPlayer senderAccount = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());
        final UUID lastMessagePlayer = senderAccount.getLastPrivateMessagePlayer();

        if (lastMessagePlayer == null) {
            player.sendMessage(HyriLanguageMessage.get("message.private.no-player-to-reply").getForPlayer(player));
            return;
        }

        sendPrivateMessage(player, HyriAPI.get().getPlayerManager().getPlayer(lastMessagePlayer), message);
    }

    public static void sendPrivateMessage(Player sender, IHyriPlayer target, String message) {
        final IHyriPlayer senderAccount = HyriAPI.get().getPlayerManager().getPlayer(sender.getUniqueId());
        final UUID senderId = sender.getUniqueId();
        final UUID targetId = target.getUniqueId();

        if (targetId.equals(senderId)) {
            sender.sendMessage(HyriLanguageMessage.get("message.private.cant-send-message-to-you").getForPlayer(sender));
            return;
        }

        if (!target.isOnline()) {
            sender.sendMessage(HyriLanguageMessage.get("message.private.not-online").getForPlayer(sender).replace("%player%", target.getName()));
            return;
        }

        final IHyriPlayerSettings settings = target.getSettings();
        final HyriPrivateMessagesLevel level = settings.getPrivateMessagesLevel();

        if (level == HyriPrivateMessagesLevel.NONE) {
            sender.sendMessage(HyriLanguageMessage.get("message.private.doesnt-accept").getForPlayer(sender).replace("%player%", target.getName()));
            return;
        } else if (level == HyriPrivateMessagesLevel.FRIENDS) {
            HyriAPI.get().getFriendManager().createHandlerAsync(targetId).whenComplete((friendHandler, throwable) -> {
               if (friendHandler.areFriends(senderId)) {
                   sendPrivateMessage0(target, senderAccount, message);
                   return;
               }

                sender.sendMessage(HyriLanguageMessage.get("message.private.accept-friends").getForPlayer(sender).replace("%player%", target.getName()));
            });
            return;
        }

        sendPrivateMessage0(target, senderAccount, message);
    }

    private static void sendPrivateMessage0(IHyriPlayer target, IHyriPlayer sender, String message) {
        target.setLastPrivateMessagePlayer(sender.getUniqueId());
        target.update();

        PlayerUtil.sendComponent(target.getUniqueId(), PrivateMessageUtil.createReceivedMessage(target, sender, message));
        PlayerUtil.sendComponent(sender.getUniqueId(), PrivateMessageUtil.createSentMessage(target, sender, message));
    }

    static TextComponent createReceivedMessage(IHyriPlayer target, IHyriPlayer sender, String message) {
        final String reply = HyriLanguageMessage.get("message.private.reply").getForPlayer(target).replace("%player%", sender.getNameWithRank());

        final ComponentBuilder builder = new ComponentBuilder(HyriLanguageMessage.get("message.private.received").getForPlayer(target).replace("%player%", sender.getNameWithRank()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(reply))).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/r "))
                .append(ChatColor.AQUA + " " + message)
                .event((HoverEvent) null).event((ClickEvent) null);

        return new TextComponent(builder.create());
    }

    static TextComponent createSentMessage(IHyriPlayer target, IHyriPlayer sender, String message) {
        final ComponentBuilder builder = new ComponentBuilder(HyriLanguageMessage.get("message.private.sent").getForPlayer(sender).replace("%player%", target.getNameWithRank()))
                .append(ChatColor.AQUA + " " + message);

        return new TextComponent(builder.create());
    }

}
