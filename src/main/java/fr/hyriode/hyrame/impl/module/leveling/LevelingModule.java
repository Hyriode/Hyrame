package fr.hyriode.hyrame.impl.module.leveling;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.utils.Symbols;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 30/04/2022 at 23:29
 */
public class LevelingModule {

    public LevelingModule() {
        HyriAPI.get().getNetworkManager().getEventBus().register(new LevelingListener(this));
    }

    public void onLevelUp(Player player, int oldLevel, int newLevel) {
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
        player.sendMessage(HyriLanguageMessage.get("message.leveling.new").getValue(player)
                .replace("%old_level%", String.valueOf(oldLevel))
                .replace("%new_level%", String.valueOf(newLevel)));
    }

}
