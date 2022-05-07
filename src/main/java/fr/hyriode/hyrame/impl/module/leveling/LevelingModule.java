package fr.hyriode.hyrame.impl.module.leveling;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
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
        final ComponentBuilder builder = new ComponentBuilder(Symbols.HYPHENS_LINE).color(ChatColor.DARK_AQUA).strikethrough(true).append("\n").reset();
        final String levelUpLine = "LEVEL UP";
        final String messageLine = HyriLanguageMessage.get("message.leveling.new").getForPlayer(player)
                .replace("%old_level%", String.valueOf(oldLevel))
                .replace("%new_level%", String.valueOf(newLevel));

        for (int i = 0; i <= (Symbols.HYPHENS_LINE.length() - ChatColor.stripColor(levelUpLine).length()) / 2 ; i++) {
            builder.append("  ");
        }

        builder.append(levelUpLine).color(ChatColor.AQUA).bold(true).append("\n").reset()
                .append(messageLine)
                .append("\n")
                .append(Symbols.HYPHENS_LINE).color(ChatColor.DARK_AQUA).strikethrough(true);

        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
        player.spigot().sendMessage(builder.create());
    }

}
