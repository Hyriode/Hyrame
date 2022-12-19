package fr.hyriode.hyrame.impl.chat;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.listener.HyriListener;
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
public class ChatListener extends HyriListener<HyramePlugin> {

    public ChatListener(HyramePlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(AsyncPlayerChatEvent event) {
        this.plugin.getHyrame().getChatManager().onEvent(event);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        final String command = event.getMessage().substring(1);

        if (this.isBlockedCommand(command)) {
            player.sendMessage(HyriLanguageMessage.get("message.command.not-enabled").getValue(player));

            event.setCancelled(true);
        }
    }

    private boolean isBlockedCommand(String command) {
        for (String blockedCommand : this.plugin.getHyrame().getCommandManager().getCommandBlocker().getBlockedCommands()) {
            if (command.startsWith(blockedCommand)) {
                return true;
            }
        }
        return false;
    }

}
