package fr.hyriode.hyrame.impl.listener.hyrame;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.impl.tab.HyriTabManager;
import fr.hyriode.hyrame.language.HyriLanguageUpdatedEvent;
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
    public void onLanguagesUpdated(HyriLanguageUpdatedEvent event) {
        final Player player = event.getPlayer();
        final Hyrame hyrame = this.plugin.getHyrame();
        final HyriTabManager tabManager = hyrame.getTabManager();

        tabManager.onLogout(player);
        tabManager.onLogin(player);
    }

}
