package fr.hyriode.hyrame.impl.game.chat;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.chat.channel.HyriChatChannel;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.api.rank.type.HyriPlayerRankType;
import fr.hyriode.hyrame.chat.IHyriChatHandler;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameSpectator;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.utils.BroadcastUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 16/12/2021 at 20:45
 */
public class GameChatHandler implements IHyriChatHandler {

    private final Set<UUID> saidGG = new HashSet<>();

    private boolean cancelled;

    private final Hyrame hyrame;

    public GameChatHandler(Hyrame hyrame) {
        this.hyrame = hyrame;
    }

    @Override
    public boolean onChat(AsyncPlayerChatEvent event) {
        final HyriGame<?> game = this.hyrame.getGameManager().getCurrentGame();

        if (game == null || this.cancelled) {
            return true;
        }

        event.setCancelled(true);

        final String message = event.getMessage();
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        final IHyriPlayer account = IHyriPlayer.get(uuid);
        final IHyriPlayerSession session = IHyriPlayerSession.get(uuid);
        final HyriGamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
        final HyriGameSpectator spectator = game.getSpectator(uuid);

        if (spectator != null) {
            game.sendMessageToSpectators(target -> String.format(this.format(), account.getNameWithRank(), message));
            return false;
        }

        if (gamePlayer == null) {
            return true;
        }

        final HyriGameTeam team = gamePlayer.getTeam();

        if (!account.getSettings().getChatChannel().hasAccess(account)) {
            account.getSettings().setChatChannel(HyriChatChannel.GLOBAL);
            account.update();
        }

        final HyriChatChannel channel = account.getSettings().getChatChannel();

        if (channel == HyriChatChannel.GLOBAL) {
            if (game.getState() == HyriGameState.PLAYING) {
                final ChatColor color = team != null && team.getColor() != null ? team.getColor().getChatColor() : null;

                if (gamePlayer.isSpectator()) {
                    game.sendMessageToSpectators(target -> String.format(this.format(), color != null ? color + "[" + team.getDisplayName().getValue(target) + color + "] " + session.getNameWithRank() : session.getNameWithRank(), message));
                } else if (color == null) {
                    BroadcastUtil.broadcast(target -> String.format(this.format(), session.getNameWithRank(), message));
                } else if (gamePlayer.getTeam().getTeamSize() == 1) {
                    BroadcastUtil.broadcast(target -> String.format(this.format(), color != null ? color + "[" + team.getDisplayName().getValue(target) + color + "] " + session.getNameWithRank() : session.getNameWithRank(), message));
                } else if (message.startsWith("!")) {
                    BroadcastUtil.broadcast(target -> String.format(this.format(), HyrameMessage.GAME_GLOBAL_MESSAGE.asString(target) + (color != null ? color + "[" + team.getDisplayName().getValue(target) + color + "] " : "") + session.getNameWithRank(), message.substring(1)));
                } else {
                    team.sendMessage(target -> String.format(this.format(), HyrameMessage.GAME_TEAM_MESSAGE.asString(target) + session.getNameWithRank(), message));
                }
            } else {
                if (game.getState() == HyriGameState.ENDED && message.equalsIgnoreCase("gg") && !this.saidGG.contains(uuid) && account.getRank().isSuperior(HyriPlayerRankType.VIP_PLUS)) {
                    account.getHyris().add(5)
                            .withMessage(true)
                            .withReason("Fairplay")
                            .withMultiplier(false)
                            .exec();
                    account.update();

                    this.saidGG.add(uuid);

                    if (!session.hasNickname()) {
                        event.setMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "GG");
                    }
                }

                BroadcastUtil.broadcast(target -> {
                    String messageStart = "";

                    if (team != null && team.getColor() != null) {
                        final ChatColor color = team.getColor().getChatColor();

                        messageStart = color + "[" + team.getDisplayName().getValue(target) + color + "] ";
                    }

                    messageStart += session.getNameWithRank();

                    return String.format(this.format(), messageStart, message);
                });
            }
        } else {
            HyriAPI.get().getChatChannelManager().sendMessage(channel, uuid, event.getMessage(), false);
        }
        return false;
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
