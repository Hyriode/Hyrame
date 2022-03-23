package fr.hyriode.hyrame.impl.game.chat;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.chat.IHyriChatHandler;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.hyrame.utils.RankUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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
                final IHyriLanguageManager languageManager = this.hyrame.getLanguageManager();
                final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());
                final HyriGamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
                final HyriGameTeam team = gamePlayer.getTeam();
                final HyriLanguageMessage teamChatPrefix = languageManager.getMessage("team.chat.prefix");

                if (game.getState() == HyriGameState.PLAYING) {
                    if (gamePlayer.isSpectator()) {
                        game.sendMessageToSpectators(target -> String.format(this.format(), RankUtil.formatRankForPlayer(account.getRank(), target) + player.getDisplayName(), message), true);
                    } else if (gamePlayer.isDead()) {
                        player.sendMessage(ChatColor.RED + languageManager.getValue(player, "error.chat.dead"));
                    } else if (gamePlayer.getTeam().getTeamSize() == 1) {
                        game.sendMessageToAll(target -> String.format(this.format(), team.getColor().getChatColor() + "[" + team.getDisplayName().getForPlayer(target) + "] " + RankUtil.formatRankForPlayer(account.getRank(), target) + player.getDisplayName(), message));
                    } else if (message.startsWith("!")) {
                        game.sendMessageToAll(target -> String.format(this.format(), ChatColor.DARK_AQUA + "[Global] " + team.getColor().getChatColor() + "[" + team.getDisplayName().getForPlayer(target) + "] " + RankUtil.formatRankForPlayer(account.getRank(), target) + player.getDisplayName(), message.substring(1)));
                    } else {
                        team.sendMessage(target -> String.format(this.format(), ChatColor.DARK_AQUA + "[" + teamChatPrefix.getForPlayer(target) + "] " + RankUtil.formatRankForPlayer(account.getRank(), target) + player.getDisplayName(), message));
                    }
                } else {
                    game.sendMessageToAll(target -> {
                        String messageStart = "";

                        if (team != null) {
                            messageStart = team.getColor().getChatColor() + "[" + team.getDisplayName().getForPlayer(target) + "] ";
                        }

                        messageStart += RankUtil.formatRankForPlayer(account.getRank(), target) + player.getDisplayName();

                        return String.format(this.format(), messageStart, message);
                    });
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
