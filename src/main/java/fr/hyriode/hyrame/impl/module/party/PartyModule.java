package fr.hyriode.hyrame.impl.module.party;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.party.HyriPartyInvitation;
import fr.hyriode.api.party.IHyriParty;
import fr.hyriode.api.party.IHyriPartyManager;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.chat.HyriMessageEvent;
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
 * on 28/04/2022 at 07:32
 */
public class PartyModule {

    public PartyModule() {
        HyriAPI.get().getPubSub().subscribe(IHyriPartyManager.REDIS_CHANNEL, new PartyReceiver(this));
        HyriAPI.get().getNetworkManager().getEventBus().register(new PartyListener());
    }

    public void sendPartyMessage(IHyriParty party, Player player, String message) {
        final UUID playerId = player.getUniqueId();
        final HyriMessageEvent event = new HyriMessageEvent(playerId, message);

        HyriAPI.get().getEventBus().publish(event);

        if (!event.isCancelled()) {
            if (party.isChatEnabled() || party.getRank(playerId).canMute()) {
                party.sendMessage(playerId, message);
            } else {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.chat-muted").getForPlayer(player))));
            }
        }
    }

    public void onInvitation(HyriPartyInvitation invitation) {
        final Player receiver = Bukkit.getPlayer(invitation.getReceiver());

        if (receiver == null) {
            return;
        }

        final IHyriPlayer sender = HyriAPI.get().getPlayerManager().getPlayer(invitation.getSender());

        receiver.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.invitation-received").getForPlayer(receiver).replace("%player%", sender.getNameWithRank()))
                .append("\n")
                .append("[" + HyriLanguageMessage.get("button.accept").getForPlayer(receiver) + "]").color(ChatColor.GREEN)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(HyriLanguageMessage.get("hover.party.accept").getForPlayer(receiver))))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p join " + sender.getName()))
                .append(" ")
                .append("[" + HyriLanguageMessage.get("button.deny").getForPlayer(receiver) + "]").color(ChatColor.RED)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(HyriLanguageMessage.get("hover.party.deny").getForPlayer(receiver))))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p deny " + sender.getName()))));
    }

    public static BaseComponent[] createMessage(Consumer<ComponentBuilder> append) {
        final ComponentBuilder builder = new ComponentBuilder(Symbols.HYPHENS_LINE).color(ChatColor.DARK_AQUA).strikethrough(true)
                .append("\n").strikethrough(false);

        append.accept(builder);

        builder.append(Symbols.HYPHENS_LINE).color(ChatColor.DARK_AQUA).strikethrough(true).event((ClickEvent) null).event((HoverEvent) null);

        return builder.create();
    }
}
