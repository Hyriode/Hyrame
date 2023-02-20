package fr.hyriode.hyrame.impl.listener.hyrame;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.api.player.event.RankUpdatedEvent;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.impl.tablist.RanksHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 16/12/2021 at 19:45
 */
public class AccountListener {

    private final HyramePlugin plugin;

    public AccountListener(HyramePlugin plugin) {
        this.plugin = plugin;

        HyriAPI.get().getEventBus().register(this);
    }

    @HyriEventHandler
    public void onRankUpdated(RankUpdatedEvent event) {
        final Player player = Bukkit.getPlayer(event.getPlayerId());

        if (player == null) {
            return;
        }

        final Hyrame hyrame = this.plugin.getHyrame();
        final RanksHandler ranksHandler = hyrame.getTabListManager().getRanksHandler();

        ranksHandler.onLogout(player);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> ranksHandler.onLogin(player), 5L);
    }

}
