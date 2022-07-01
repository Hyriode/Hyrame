package fr.hyriode.hyrame.impl.module.friend;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.friend.HyriFriendRequest;
import fr.hyriode.api.friend.IHyriFriendHandler;
import fr.hyriode.api.friend.IHyriFriendManager;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.utils.Symbols;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/04/2022 at 08:44
 */
public class FriendModule {

    public static final int FRIENDS_LIST_SIZE = 10;

    private final IHyriFriendManager friendManager;

    public FriendModule() {
        this.friendManager = HyriAPI.get().getFriendManager();

        HyriAPI.get().getNetworkManager().getEventBus().register(new FriendListener());
        HyriAPI.get().getPubSub().subscribe(IHyriFriendManager.REDIS_CHANNEL, new FriendReceiver(this));
    }

    public void sendRequest(Player player, IHyriPlayer target) {
        final UUID playerId = player.getUniqueId();
        final UUID targetId = target.getUniqueId();

        if (this.friendManager.hasRequest(targetId, playerId)) {
            player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.friend.request-already").getForPlayer(player).replace("%player%", target.getNameWithRank()))));
            return;
        }

        HyriAPI.get().getFriendManager().sendRequest(player.getUniqueId(), target.getUniqueId());

        player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.friend.request-sent").getForPlayer(player).replace("%player%", target.getNameWithRank()))));
    }

    public void onRequest(HyriFriendRequest request) {
        final Player player = Bukkit.getPlayer(request.getReceiver());

        if (player != null) {
            final IHyriPlayer sender = HyriAPI.get().getPlayerManager().getPlayer(request.getSender());

            player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.friend.request-received").getForPlayer(player).replace("%player%", sender.getNameWithRank()))
                    .append("\n")
                    .append("[" + HyriLanguageMessage.get("button.accept").getForPlayer(player) + "]").color(ChatColor.GREEN)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(HyriLanguageMessage.get("hover.friend.accept").getForPlayer(player))))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f accept " + sender.getName()))
                    .append(" ")
                    .append("[" + HyriLanguageMessage.get("button.deny").getForPlayer(player) + "]").color(ChatColor.RED)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(HyriLanguageMessage.get("hover.friend.deny").getForPlayer(player))))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f deny " + sender.getName()))));
        }
    }

    public boolean checkRequest(Player player, IHyriPlayer sender) {
        if (!HyriAPI.get().getFriendManager().hasRequest(player.getUniqueId(), sender.getUniqueId())) {
            player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.friend.no-request").getForPlayer(player).replace("%player%", sender.getNameWithRank()))));
            return false;
        }
        return true;
    }

    public boolean checkAreFriends(IHyriFriendHandler friendHandler, Player player, IHyriPlayer target) {
        if (friendHandler.areFriends(target.getUniqueId())) {
            player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.friend.already").getForPlayer(player).replace("%player%", target.getNameWithRank()))));
            return true;
        }
        return false;
    }

    public boolean checkAreNotFriends(IHyriFriendHandler friendHandler, Player player, IHyriPlayer target) {
        if (!friendHandler.areFriends(target.getUniqueId())) {
            player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.friend.not-already").getForPlayer(player).replace("%player%", target.getNameWithRank()))));
            return true;
        }
        return false;
    }

    public static BaseComponent[] createMessage(Consumer<ComponentBuilder> append) {
        final ComponentBuilder builder = new ComponentBuilder(Symbols.HYPHENS_LINE).color(ChatColor.LIGHT_PURPLE).strikethrough(true)
                .append("\n").strikethrough(false);

        append.accept(builder);

        builder.append("\n")
                .append(Symbols.HYPHENS_LINE)
                .event((ClickEvent) null)
                .event((HoverEvent) null)
                .color(ChatColor.LIGHT_PURPLE)
                .strikethrough(true);

        return builder.create();
    }

}
