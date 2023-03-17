package fr.hyriode.hyrame.game.protocol;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.api.language.HyriLanguage;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameSpectator;
import fr.hyriode.hyrame.game.event.HyriGameWinEvent;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.utils.PlayerUtil;
import fr.hyriode.hyrame.utils.Symbols;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/02/2022 at 20:23
 */
public class HyriWinProtocol extends HyriGameProtocol implements Listener {

    private final HyriGame<?> game;

    public HyriWinProtocol(IHyrame hyrame) {
        super(hyrame, "win");
        this.game = IHyrame.get().getGame();
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
        final List<HyriGameSpectator> players = new ArrayList<>();

        players.addAll(this.game.getPlayers());
        players.addAll(this.game.getSpectators());

        for (HyriGameSpectator player : players) {
            final Player target = player.getPlayer();

            if (!player.isSpectator()) {
                player.setSpectator(true);
            }

            PlayerUtil.resetPotionEffects(target);

            Bukkit.getScheduler().runTaskLater(this.game.getPlugin(), player::show, 1L);
        }

        for (HyriGameTeam team : this.game.getTeams()) {
            if (team != winner) {
                team.sendTitle(HyrameMessage.GAME_DEFEAT::asString, target -> "", 0, 60, 5);
            }
        }

        winner.sendTitle(HyrameMessage.GAME_VICTORY::asString, target -> "", 0, 20 * 8, 5);
    }

}
