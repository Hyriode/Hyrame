package fr.hyriode.hyrame.impl.tab;

import fr.hyriode.hyrame.IHyrameConfiguration;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.tab.Tab;
import fr.hyriode.hyrame.utils.ThreadPool;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.hyriapi.player.IHyriPlayer;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 16/12/2021 at 18:15
 */
public class HyriTabManager {

    private final Map<HyriLanguage, HyriTabHandler> handlers;
    private final Map<HyriLanguage, Tab> tabs;

    private final Hyrame hyrame;

    public HyriTabManager(Hyrame hyrame) {
        this.hyrame = hyrame;
        this.handlers = new HashMap<>();
        this.tabs = new HashMap<>();

        for (HyriLanguage language : HyriLanguage.values()) {
            this.handlers.put(language, new HyriTabHandler(this.hyrame, language));
            this.tabs.put(language, new HyriDefaultTab(this.hyrame, language));
        }
    }

    public void onLogin(Player player) {
        ThreadPool.EXECUTOR.execute(() -> {
            final IHyrameConfiguration configuration = this.hyrame.getConfiguration();
            final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());

            if (this.hyrame.getConfiguration().areRanksInTabList()) {
                for (HyriTabHandler handler : this.handlers.values()) {
                    handler.onLogin(player);
                }
            }

            final Tab tab = configuration.getTab() == null ? this.getTabForPlayer(account) : configuration.getTab();

            tab.send(player);
        });
    }

    public void onLogout(Player player) {
        ThreadPool.EXECUTOR.execute(() -> {
            for (HyriTabHandler handler : this.handlers.values()) {
                handler.onLogout(player);
            }
        });
    }

    public void enableTabList() {
        for (HyriTabHandler handler : this.handlers.values()) {
            handler.enable();
        }
    }

    public void disableTabList() {
        for (HyriTabHandler handler : this.handlers.values()) {
            handler.disable();
        }
    }

    private Tab getTabForPlayer(IHyriPlayer player) {
        final HyriLanguage language = player.getSettings().getLanguage();

        return this.tabs.get(language) != null ? this.tabs.get(language) : this.tabs.get(HyriLanguage.EN);
    }

}
