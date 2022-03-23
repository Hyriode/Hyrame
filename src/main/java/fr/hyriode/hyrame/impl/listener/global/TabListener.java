package fr.hyriode.hyrame.impl.listener.global;

import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.listener.HyriListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 16:11
 */
public class TabListener extends HyriListener<HyramePlugin> {

    public TabListener(HyramePlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        this.plugin.getHyrame().getTabManager().onLogin(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent event) {
        this.plugin.getHyrame().getTabManager().onLogout(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onKick(PlayerKickEvent event) {
        this.plugin.getHyrame().getTabManager().onLogout(event.getPlayer());
    }

}
