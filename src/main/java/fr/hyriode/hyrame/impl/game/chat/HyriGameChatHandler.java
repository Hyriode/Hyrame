package fr.hyriode.hyrame.impl.game.chat;

import fr.hyriode.hyrame.chat.IHyriChatHandler;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.utils.RankUtil;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.hyriapi.player.IHyriPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.stream.Collectors;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 16/12/2021 at 20:45
 */
public class HyriGameChatHandler implements IHyriChatHandler {

    private boolean cancelled;

    private final Hyrame hyrame;

    public HyriGameChatHandler(Hyrame hyrame) {
        this.hyrame = hyrame;
    }

    @Override
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        if (!this.cancelled) {
            final Player player = event.getPlayer();
            final String message = event.getMessage();
            final HyriGame<?> game = this.hyrame.getGameManager().getCurrentGame();

            if (game != null) {
                final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());
                final HyriGamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
                final HyriGameTeam team = gamePlayer.getTeam();
                final HyriLanguageMessage teamChatPrefix = this.hyrame.getLanguageManager().getMessage("team.chat.prefix");

                if (game.getState() == HyriGameState.PLAYING) {
                    if (message.startsWith("!")) {
                        for (Player target : Bukkit.getOnlinePlayers()) {
                            final String messageStart = ChatColor.DARK_AQUA + "[Global] " + team.getColor().getColor() + "[" + team.getDisplayName().getForPlayer(target) + "] " + RankUtil.formatRankForPlayer(account.getRank(), target) + player.getDisplayName();

                            target.sendMessage(String.format(this.format(), messageStart, message.substring(1)));
                        }
                    } else {
                        for (Player target : team.getPlayers().stream().map(HyriGamePlayer::getPlayer).collect(Collectors.toList())) {
                            if (target.isOnline()) {
                                final String messageStart = ChatColor.DARK_AQUA + "[" + teamChatPrefix.getForPlayer(target) + "] " + RankUtil.formatRankForPlayer(account.getRank(), target) + player.getDisplayName();

                                target.sendMessage(String.format(this.format(), messageStart, message));
                            }
                        }
                    }
                } else {
                    for (Player target : Bukkit.getOnlinePlayers()) {
                        if (target.isOnline()) {
                            String messageStart = "";

                            if (team != null) {
                                messageStart = team.getColor().getColor() + "[" + teamChatPrefix.getForPlayer(target) + "] ";
                            }

                            messageStart += RankUtil.formatRankForPlayer(account.getRank(), target) + player.getDisplayName();

                            target.sendMessage(String.format(this.format(), messageStart, message));
                        }
                    }
                }
            }
        }
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
