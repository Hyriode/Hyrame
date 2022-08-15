package fr.hyriode.hyrame.game.protocol;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.api.language.HyriLanguage;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.event.HyriGameWinEvent;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.utils.PlayerUtil;
import fr.hyriode.hyrame.utils.Symbols;
import org.bukkit.ChatColor;
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
    private static final HyriLanguageMessage DEFEAT = new HyriLanguageMessage("")
            .addValue(HyriLanguage.EN, "DEFEAT")
            .addValue(HyriLanguage.FR, "DEFAITE");

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
        this.game.getProtocolManager().disableProtocol(HyriDeathProtocol.class);

        final HyriGameTeam winner = event.getWinner();

        for (HyriGamePlayer player : this.game.getPlayers()) {
            final Player target = player.getPlayer();

            if (!player.isSpectator()) {
                player.setSpectator(true);
            }

            PlayerUtil.resetPotionEffects(target);

            player.show();
        }

        for (HyriGameTeam team : this.game.getTeams()) {
            if (team != winner) {
                team.sendTitle(target -> ChatColor.RED + DEFEAT.getValue(target), target -> "", 0, 60, 5);
            }
        }

        winner.sendTitle(target -> ChatColor.GOLD + Symbols.SPARKLES + " " + ChatColor.BOLD + VICTORY.getValue(target) + ChatColor.RESET + ChatColor.GOLD + " " + Symbols.SPARKLES, target -> "", 0, 20 * 8, 5);
    }

}
