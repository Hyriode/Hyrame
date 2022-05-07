package fr.hyriode.hyrame.impl.tab;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.hyrame.IHyrameConfiguration;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.tab.Tab;
import fr.hyriode.hyrame.utils.ThreadUtil;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 16/12/2021 at 18:15
 */
public class HyriTabManager {

    private final HyriTabHandler handler;
    private final Map<UUID, HyriDefaultTab> tabs;

    public HyriTabManager(Hyrame hyrame) {
        this.handler = new HyriTabHandler(hyrame);
        this.tabs = new HashMap<>();

        ThreadUtil.EXECUTOR.scheduleAtFixedRate(() -> {
            for (HyriDefaultTab tab : this.tabs.values()) {
                tab.update();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void onLogin(Player player) {
        this.tabs.put(player.getUniqueId(), new HyriDefaultTab(player));
        this.handler.onLogin(player);
    }

    public void onLogout(Player player) {
        this.tabs.remove(player.getUniqueId());
        this.handler.onLogout(player);
    }

    public void enableTabList() {
        this.handler.enable();
    }

    public void disableTabList() {
        this.handler.disable();
    }

    public HyriTabHandler getHandler() {
        return this.handler;
    }

}
