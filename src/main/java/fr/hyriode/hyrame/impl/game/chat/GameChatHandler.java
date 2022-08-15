package fr.hyriode.hyrame.impl.game.chat;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.chat.channel.HyriChatChannel;
import fr.hyriode.api.chat.channel.IHyriChatChannelManager;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.rank.type.HyriPlayerRankType;
import fr.hyriode.hyrame.chat.IHyriChatHandler;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.impl.Hyrame;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 16/12/2021 at 20:45
 */
public class GameChatHandler implements IHyriChatHandler {

    private final List<UUID> saidGG = new ArrayList<>();

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
        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(uuid);
        final HyriGamePlayer gamePlayer = game.getPlayer(player.getUniqueId());

        if (gamePlayer == null || message == null || message.isEmpty()) {
            return true;
        }

        final HyriGameTeam team = gamePlayer.getTeam();
        final HyriLanguageMessage teamChatPrefix = HyriLanguageMessage.get("team.chat.prefix");
        final String channel = account.getSettings().getChatChannel();

        if (!IHyriChatChannelManager.canPlayerAccessChannel(channel, account)) {
            player.sendMessage(ChatColor.RED + HyriLanguageMessage.get("message.error.chat.cant-talk").getValue(player));
            account.getSettings().setChatChannel(HyriChatChannel.GLOBAL.getChannel());
            account.update();
        }

        if (channel.equalsIgnoreCase(HyriChatChannel.GLOBAL.getChannel())) {
            if (game.getState() == HyriGameState.PLAYING) {
                final ChatColor color = team.getColor() != null ? team.getColor().getChatColor() : null;

                if (gamePlayer.isSpectator()) {
                    game.sendMessageToSpectators(target -> String.format(this.format(), account.getNameWithRank(true), message), true);
                } else if (gamePlayer.isDead()) {
                    player.sendMessage(ChatColor.RED + HyriLanguageMessage.get("error.chat.dead").getValue(player));
                } else if (color == null) {
                    game.sendMessageToAll(target -> String.format(this.format(), account.getNameWithRank(true), message));
                } else if (gamePlayer.getTeam().getTeamSize() == 1) {
                    game.sendMessageToAll(target -> String.format(this.format(), color + "[" + team.getDisplayName().getValue(target) + color + "] " + account.getNameWithRank(true), message));
                } else if (message.startsWith("!")) {
                    game.sendMessageToAll(target -> String.format(this.format(), ChatColor.DARK_AQUA + "[Global] " + color + "[" + team.getDisplayName().getValue(target) + color + "] " + account.getNameWithRank(true), message.substring(1)));
                } else {
                    team.sendMessage(target -> String.format(this.format(), ChatColor.DARK_AQUA + "[" + teamChatPrefix.getValue(target) + "] " + account.getNameWithRank(true), message));
                }
            } else {
                if (game.getState() == HyriGameState.ENDED && message.equalsIgnoreCase("gg") && !this.saidGG.contains(uuid)) {
                    if (account.getRank().isSuperior(HyriPlayerRankType.VIP_PLUS) && !account.hasNickname()) {
                        account.getHyris().add(ThreadLocalRandom.current().nextInt(1, 6))
                                .withReason("Fairplay")
                                .withMessage(true)
                                .withMultiplier(false)
                                .exec();
                        account.update();

                        this.saidGG.add(uuid);

                        event.setMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "GG");
                    }
                }

                game.sendMessageToAll(target -> {
                    String messageStart = "";

                    if (team != null && team.getColor() != null) {
                        final ChatColor color = team.getColor().getChatColor();

                        messageStart = color + "[" + team.getDisplayName().getValue(target) + color + "] ";
                    }

                    messageStart += account.getNameWithRank(true);

                    return String.format(this.format(), messageStart, event.getMessage());
                });
            }
        } else {
            HyriAPI.get().getChatChannelManager().sendMessage(channel, event.getMessage(), uuid);
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
