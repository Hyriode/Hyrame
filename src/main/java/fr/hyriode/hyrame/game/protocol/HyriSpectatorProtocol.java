package fr.hyriode.hyrame.game.protocol;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.event.player.HyriGameSpectatorEvent;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.utils.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/02/2022 at 20:23
 */
public class HyriSpectatorProtocol extends HyriGameProtocol implements Listener {

    private final JavaPlugin plugin;

    public HyriSpectatorProtocol(IHyrame hyrame, JavaPlugin plugin) {
        super(hyrame, "spectator");
        this.plugin = plugin;
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
    public void onSpectator(HyriGameSpectatorEvent event) {
        final HyriGamePlayer gamePlayer = event.getGamePlayer();
        final Player player = gamePlayer.getPlayer();

        PlayerUtil.resetPlayer(player, true);
        PlayerUtil.addSpectatorAbilities(player);

        gamePlayer.hide();

        player.spigot().setCollidesWithEntities(false);

        HyriGameItems.SPECTATOR_TELEPORTER.give(this.hyrame, player, 0);
        HyriGameItems.SPECTATOR_SETTINGS.give(this.hyrame, player, 1);
        HyriGameItems.RESTART_GAME.give(this.hyrame, player, 4);
        HyriGameItems.LEAVE.give(this.hyrame, player, 8);
    }


}