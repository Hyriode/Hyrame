package fr.hyriode.hyrame.impl.module.chat.message;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.settings.HyriSettingsLevel;
import fr.hyriode.api.sound.HyriSound;
import fr.hyriode.api.sound.HyriSoundPacket;
import fr.hyriode.hyrame.chat.event.PrivateMessageEvent;
import fr.hyriode.hyrame.utils.PlayerUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 15:35
 */
public class PrivateMessageModule {

    public void replyToMessage(Player player, String message) {
        final IHyriPlayer senderAccount = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());
        final UUID lastMessagePlayer = senderAccount.getLastPrivateMessagePlayer();

        if (lastMessagePlayer == null) {
            player.sendMessage(HyriLanguageMessage.get("message.private.no-player-to-reply").getValue(player));
            return;
        }

        this.sendPrivateMessage(player, HyriAPI.get().getPlayerManager().getPlayer(lastMessagePlayer), message);
    }

    public void sendPrivateMessage(Player sender, IHyriPlayer target, String message) {
        final IHyriPlayer senderAccount = HyriAPI.get().getPlayerManager().getPlayer(sender.getUniqueId());
        final UUID senderId = sender.getUniqueId();
        final UUID targetId = target.getUniqueId();

        if (!target.isOnline()) {
            sender.sendMessage(HyriLanguageMessage.get("message.private.not-online").getValue(sender).replace("%player%", target.getNameWithRank()));
            return;
        }

        if (targetId.equals(senderId)) {
            sender.sendMessage(HyriLanguageMessage.get("message.private.cant-send-message-to-you").getValue(sender));
            return;
        }

        this.sendPrivateMessage0(target, senderAccount, message);
    }

    private void sendPrivateMessage0(IHyriPlayer target, IHyriPlayer sender, String message) {
        final UUID senderId = sender.getUniqueId();
        final UUID targetId = target.getUniqueId();
        final PrivateMessageEvent event = new PrivateMessageEvent(senderId, targetId);

        HyriAPI.get().getEventBus().publish(event);

        if (event.isCancelled()) {
            return;
        }

        final HyriSettingsLevel sendLevel = target.getSettings().getPrivateMessagesLevel();
        final HyriSettingsLevel soundLevel = target.getSettings().getPrivateMessagesSoundLevel();
        final boolean areFriends = HyriAPI.get().getFriendManager().createHandler(targetId).areFriends(senderId);
        final boolean isStaff = sender.getRank().isStaff();

        target.setLastPrivateMessagePlayer(sender.getUniqueId());
        target.update();

        if (sendLevel == HyriSettingsLevel.ALL || (sendLevel == HyriSettingsLevel.FRIENDS && areFriends) || isStaff) {
            if (soundLevel == HyriSettingsLevel.ALL || (soundLevel == HyriSettingsLevel.FRIENDS && areFriends) || isStaff) {
                HyriSoundPacket.send(targetId, HyriSound.ORB_PICKUP, 1.0F, 1.5F);
            }

            PlayerUtil.sendComponent(targetId, this.createReceivedMessage(target, sender, message));
            PlayerUtil.sendComponent(senderId, this.createSentMessage(target, sender, message));
        } else {
            Bukkit.getPlayer(senderId).sendMessage(HyriLanguageMessage.get("message.private.doesnt-accept").getValue(sender).replace("%player%", target.getNameWithRank()));
        }
    }

    private TextComponent createReceivedMessage(IHyriPlayer target, IHyriPlayer sender, String message) {
        final String reply = HyriLanguageMessage.get("message.private.reply").getValue(target).replace("%player%", sender.getNameWithRank());

        final ComponentBuilder builder = new ComponentBuilder(HyriLanguageMessage.get("message.private.received").getValue(target).replace("%player%", sender.getNameWithRank()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(reply))).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/r "))
                .append(" " + message).color(net.md_5.bungee.api.ChatColor.AQUA)
                .event((HoverEvent) null).event((ClickEvent) null);

        return new TextComponent(builder.create());
    }

    private TextComponent createSentMessage(IHyriPlayer target, IHyriPlayer sender, String message) {
        final ComponentBuilder builder = new ComponentBuilder(HyriLanguageMessage.get("message.private.sent").getValue(sender).replace("%player%", target.getNameWithRank()))
                .append(" " + message).color(net.md_5.bungee.api.ChatColor.AQUA);

        return new TextComponent(builder.create());
    }

}
