package fr.hyriode.hyrame.impl.module.leveling;

import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.api.leveling.event.HyriGainLevelEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 30/04/2022 at 23:29
 */
public class LevelingListener {

    private final LevelingModule levelingModule;

    public LevelingListener(LevelingModule levelingModule) {
        this.levelingModule = levelingModule;
    }

    @HyriEventHandler
    public void onNetworkLevelUp(HyriGainLevelEvent event) {
        if (!event.getLeveling().equals("network")) {
            return;
        }

        final Player player = Bukkit.getPlayer(event.getPlayer());

        if (player != null) {
            this.levelingModule.onLevelUp(player, event.getOldLevel(), event.getNewLevel());
        }
    }

}
