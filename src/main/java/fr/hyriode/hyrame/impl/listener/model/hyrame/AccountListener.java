package fr.hyriode.hyrame.impl.listener.model.hyrame;

import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.impl.tab.HyriTabManager;
import fr.hyriode.hyrame.event.HyriLanguagesUpdatedEvent;
import fr.hyriode.hyrame.listener.HyriListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 16/12/2021 at 19:45
 */
public class AccountListener extends HyriListener<HyramePlugin> {

    public AccountListener(HyramePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onLanguagesUpdated(HyriLanguagesUpdatedEvent event) {
        final Player player = event.getPlayer();
        final Hyrame hyrame = this.plugin.getHyrame();
        final HyriTabManager tabManager = hyrame.getTabManager();

        tabManager.onLogout(player);
        tabManager.onLogin(player);
    }

}
