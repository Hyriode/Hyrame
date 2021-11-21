package fr.hyriode.hyrame.impl.listener.model.global;

import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.listener.HyriListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 16:11
 */
public class ChatHandler extends HyriListener<HyramePlugin> {

    public ChatHandler(HyramePlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(AsyncPlayerChatEvent event) {
        if (this.plugin.getHyrame().getConfiguration().isDefaultChatFormat()) {
            event.setFormat("%s" + ChatColor.WHITE + ": %s");
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        final String command = event.getMessage().substring(1);

        if (this.isBlockedCommand(command)) {
            player.sendMessage(this.plugin.getHyrame().getLanguageManager().getMessageValueForPlayer(player, "command.not.enabled"));
            event.setCancelled(true);
        }
    }

    private boolean isBlockedCommand(String command) {
        for (String blockedCommand : this.plugin.getHyrame().getCommandBlocker().getBlockedCommands()) {
            if (command.startsWith(blockedCommand)) {
                return true;
            }
        }
        return false;
    }

}
