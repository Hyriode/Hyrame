package fr.hyriode.hyrame.game.protocol;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.event.player.HyriGameSpectatorEvent;
import fr.hyriode.hyrame.utils.PlayerUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;

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

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);

        PlayerUtil.resetPlayer(player, true);

        gamePlayer.hide();

        // TODO Give spec items
    }


}
