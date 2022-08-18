package fr.hyriode.hyrame.game.protocol;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameSpectator;
import fr.hyriode.hyrame.game.event.player.HyriGameReconnectedEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameSpectatorEvent;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.utils.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import static fr.hyriode.hyrame.game.event.player.HyriGameSpectatorEvent.Action;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/02/2022 at 20:23
 */
public class HyriSpectatorProtocol extends HyriGameProtocol implements Listener {

    public HyriSpectatorProtocol(IHyrame hyrame) {
        super(hyrame, "spectator");
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
    public void onPlayerJoin(HyriGameReconnectedEvent event) {
        for (HyriGamePlayer gamePlayer : this.hyrame.getGameManager().getCurrentGame().getPlayers()) {
            if (gamePlayer.isSpectator()) {
                gamePlayer.hide();
            }
        }
    }

    @HyriEventHandler
    public void onSpectator(HyriGameSpectatorEvent event) {
        final HyriGameSpectator spectator = event.getSpectator();
        final Player player = spectator.getPlayer();

        if (event.getAction() == Action.ADD) {
            PlayerUtil.resetPlayer(player, true);
            PlayerUtil.addSpectatorAbilities(player);

            spectator.hide();

            player.spigot().setCollidesWithEntities(false);

            HyriGameItems.SPECTATOR_TELEPORTER.give(this.hyrame, player, 0);
            HyriGameItems.SPECTATOR_SETTINGS.give(this.hyrame, player, 1);
            HyriGameItems.LEAVE.give(this.hyrame, player, 8);

            if (!HyriAPI.get().getServer().isHost()) {
                HyriGameItems.RESTART_GAME.give(this.hyrame, player, 4);
            }
        } else {
            PlayerUtil.resetPlayer(player, true);

            spectator.show();

            player.spigot().setCollidesWithEntities(true);
        }
    }


}
