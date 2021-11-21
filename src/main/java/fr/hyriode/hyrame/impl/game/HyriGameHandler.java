package fr.hyriode.hyrame.impl.game;

import fr.hyriode.hyrame.game.IHyriGameManager;
import fr.hyriode.hyrame.impl.Hyrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 09/09/2021 at 20:14
 */
class HyriGameHandler implements Listener {

    private final IHyriGameManager gameManager;

    public HyriGameHandler(Hyrame hyrame) {
        this.gameManager = hyrame.getGameManager();

        hyrame.getPlugin().getServer().getPluginManager().registerEvents(this, hyrame.getPlugin());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        this.gameManager.getCurrentGame().handleLogin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        this.gameManager.getCurrentGame().handleLogout(event.getPlayer());
    }

}
