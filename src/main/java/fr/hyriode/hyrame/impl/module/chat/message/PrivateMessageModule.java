package fr.hyriode.hyrame.impl.module.chat.message;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.settings.HyriPrivateMessagesLevel;
import fr.hyriode.api.settings.IHyriPlayerSettings;
import fr.hyriode.api.sound.HyriSound;
import fr.hyriode.api.sound.HyriSoundPacket;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.utils.PlayerUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 15:35
 */
public class PrivateMessageModule {

    private static final String REDIS_CHANNEL = "private-messages";

    public void replyToMessage(Player player, String message) {
        final IHyriPlayer senderAccount = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());
        final UUID lastMessagePlayer = senderAccount.getLastPrivateMessagePlayer();

        if (lastMessagePlayer == null) {
            player.sendMessage(HyriLanguageMessage.get("message.private.no-player-to-reply").getForPlayer(player));
            return;
        }

        this.sendPrivateMessage(player, HyriAPI.get().getPlayerManager().getPlayer(lastMessagePlayer), message);
    }

    public void sendPrivateMessage(Player sender, IHyriPlayer target, String message) {
        final IHyriPlayer senderAccount = HyriAPI.get().getPlayerManager().getPlayer(sender.getUniqueId());
        final UUID senderId = sender.getUniqueId();
        final UUID targetId = target.getUniqueId();

        if (!target.isOnline()) {
            sender.sendMessage(HyriLanguageMessage.get("message.private.not-online").getForPlayer(sender).replace("%player%", target.getNameWithRank()));
            return;
        }

        if (targetId.equals(senderId)) {
            sender.sendMessage(HyriLanguageMessage.get("message.private.cant-send-message-to-you").getForPlayer(sender));
            return;
        }

        final IHyriPlayerSettings settings = target.getSettings();
        final HyriPrivateMessagesLevel level = settings.getPrivateMessagesLevel();

        if (!senderAccount.getRank().isStaff()) {
            if (level == HyriPrivateMessagesLevel.NONE) {
                sender.sendMessage(HyriLanguageMessage.get("message.private.doesnt-accept").getForPlayer(sender).replace("%player%", target.getNameWithRank()));
                return;
            } else if (level == HyriPrivateMessagesLevel.FRIENDS) {
                HyriAPI.get().getFriendManager().createHandlerAsync(targetId).whenComplete((friendHandler, throwable) -> {
                    if (friendHandler.areFriends(senderId)) {
                        this.sendPrivateMessage0(target, senderAccount, message);
                        return;
                    }

                    sender.sendMessage(HyriLanguageMessage.get("message.private.accept-friends").getForPlayer(sender).replace("%player%", target.getNameWithRank()));
                });
                return;
            }
        }

        this.sendPrivateMessage0(target, senderAccount, message);
    }

    private void sendPrivateMessage0(IHyriPlayer target, IHyriPlayer sender, String message) {
        target.setLastPrivateMessagePlayer(sender.getUniqueId());
        target.update();

        if (target.getSettings().isPrivateMessagesSoundEnabled()) {
            HyriSoundPacket.send(target.getUniqueId(), HyriSound.ORB_PICKUP, 1.0F, 1.5F);
        }

        PlayerUtil.sendComponent(target.getUniqueId(), this.createReceivedMessage(target, sender, message));
        PlayerUtil.sendComponent(sender.getUniqueId(), this.createSentMessage(target, sender, message));
    }

    private TextComponent createReceivedMessage(IHyriPlayer target, IHyriPlayer sender, String message) {
        final String reply = HyriLanguageMessage.get("message.private.reply").getForPlayer(target).replace("%player%", sender.getNameWithRank());

        final ComponentBuilder builder = new ComponentBuilder(HyriLanguageMessage.get("message.private.received").getForPlayer(target).replace("%player%", sender.getNameWithRank()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(reply))).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/r "))
                .append(" " + message).color(net.md_5.bungee.api.ChatColor.AQUA)
                .event((HoverEvent) null).event((ClickEvent) null);

        return new TextComponent(builder.create());
    }

    private TextComponent createSentMessage(IHyriPlayer target, IHyriPlayer sender, String message) {
        final ComponentBuilder builder = new ComponentBuilder(HyriLanguageMessage.get("message.private.sent").getForPlayer(sender).replace("%player%", target.getNameWithRank()))
                .append(" " + message).color(net.md_5.bungee.api.ChatColor.AQUA);

        return new TextComponent(builder.create());
    }

}
