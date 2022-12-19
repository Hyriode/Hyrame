package fr.hyriode.hyrame.impl.scoreboard;

import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.listener.HyriListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 25/02/2022 at 18:50
 */
public class ScoreboardListener extends HyriListener<HyramePlugin> {

    public ScoreboardListener(HyramePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.plugin.getHyrame().getScoreboardManager().removeScoreboardFromPlayer(event.getPlayer());
    }

}
