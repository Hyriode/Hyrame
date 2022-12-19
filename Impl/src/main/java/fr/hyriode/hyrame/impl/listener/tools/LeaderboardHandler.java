package fr.hyriode.hyrame.impl.listener.tools;

import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.leaderboard.HyriLeaderboardDisplay;
import fr.hyriode.hyrame.listener.HyriListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by AstFaster
 * on 16/06/2022 at 20:51
 */
public class LeaderboardHandler extends HyriListener<HyramePlugin> {

    public LeaderboardHandler(HyramePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        for (HyriLeaderboardDisplay display : HyriLeaderboardDisplay.getAll()) {
            display.handleLogin(event.getPlayer());
            display.update();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        for (HyriLeaderboardDisplay display : HyriLeaderboardDisplay.getAll()) {
            display.handleLogout(event.getPlayer());
        }
    }

}
