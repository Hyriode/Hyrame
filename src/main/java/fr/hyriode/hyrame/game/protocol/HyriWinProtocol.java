package fr.hyriode.hyrame.game.protocol;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.event.HyriGameWinEvent;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.utils.PlayerUtil;
import fr.hyriode.hyrame.utils.Symbols;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/02/2022 at 20:23
 */
public class HyriWinProtocol extends HyriGameProtocol implements Listener {

    /** The victory title to show */
    private static final HyriLanguageMessage VICTORY = new HyriLanguageMessage("")
            .addValue(HyriLanguage.EN, "VICTORY")
            .addValue(HyriLanguage.FR, "VICTOIRE");

    /** The game over to show */
    private static final HyriLanguageMessage GAME_OVER = new HyriLanguageMessage("")
            .addValue(HyriLanguage.EN, "GAME OVER");

    private final HyriGame<?> game;

    public HyriWinProtocol(IHyrame hyrame, HyriGame<?> game) {
        super(hyrame, "win");
        this.game = game;
    }

    @Override
    void enable() {
        HyriAPI.get().getEventBus().register(this);
    }

    @Override
    void disable() {
        HyriAPI.get().getEventBus().unregister(this);
    }

    @HyriEventHandler
    public void onWin(HyriGameWinEvent event) {
        final HyriGameTeam winner = event.getWinner();

        for (HyriGamePlayer player : this.game.getPlayers()) {
            final Player target = player.getPlayer();

            if (!player.isSpectator()) {
                PlayerUtil.resetPlayer(target, true);

                player.setSpectator(true);
            }

            PlayerUtil.resetPotionEffects(target);
        }

        this.game.getPlayers().forEach(HyriGamePlayer::show);

        for (HyriGameTeam team : this.game.getTeams()) {
            if (team != winner) {
                team.sendTitle(target -> ChatColor.DARK_RED + GAME_OVER.getForPlayer(target), target -> "", 0, 60, 5);
            }
        }

        winner.sendTitle(target -> ChatColor.GOLD + Symbols.SPARKLES + " " + ChatColor.BOLD + VICTORY.getForPlayer(target) + ChatColor.RESET + ChatColor.GOLD + " " + Symbols.SPARKLES, target -> "", 0, 20 * 10, 5);
    }


}
