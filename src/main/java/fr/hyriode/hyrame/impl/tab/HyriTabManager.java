package fr.hyriode.hyrame.impl.tab;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.hyrame.IHyrameConfiguration;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.tab.Tab;
import fr.hyriode.hyrame.utils.ThreadUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 16/12/2021 at 18:15
 */
public class HyriTabManager {

    private final HyriTabHandler handler;
    private final Map<HyriLanguage, Tab> tabs;

    private final Hyrame hyrame;

    public HyriTabManager(Hyrame hyrame) {
        this.hyrame = hyrame;
        this.handler = new HyriTabHandler(this.hyrame);
        this.tabs = new HashMap<>();

        for (HyriLanguage language : HyriLanguage.values()) {
            this.tabs.put(language, new HyriDefaultTab(this.hyrame, language));
        }
    }

    public void onLogin(Player player) {
        ThreadUtil.EXECUTOR.execute(() -> {
            final IHyrameConfiguration configuration = this.hyrame.getConfiguration();
            final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());

            final Tab tab = configuration.getTab() == null ? this.getTabForPlayer(account) : configuration.getTab();

            tab.send(player);

            this.handler.onLogin(player);
        });
    }

    public void onLogout(Player player) {
        ThreadUtil.EXECUTOR.execute(() -> this.handler.onLogout(player));
    }

    public void enableTabList() {
        this.handler.enable();
    }

    public void disableTabList() {
        this.handler.disable();
    }

    private Tab getTabForPlayer(IHyriPlayer player) {
        final HyriLanguage language = player.getSettings().getLanguage();

        return this.tabs.get(language) != null ? this.tabs.get(language) : this.tabs.get(HyriLanguage.EN);
    }

    public HyriTabHandler getHandler() {
        return this.handler;
    }

}
