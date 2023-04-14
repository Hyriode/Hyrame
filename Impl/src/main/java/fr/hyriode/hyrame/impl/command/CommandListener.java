package fr.hyriode.hyrame.impl.command;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.listener.HyriListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Created by AstFaster
 * on 14/04/2023 at 19:24
 */
public class CommandListener extends HyriListener<HyramePlugin> {

    public CommandListener(HyramePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        final String command = event.getMessage().substring(1);
        final CommandBlocker commandBlocker = (CommandBlocker) IHyrame.get().getCommandManager().getCommandBlocker();

        if (this.isBlockedCommand(command) || commandBlocker.getCommandMap().getCommand(command) == null) {
            player.sendMessage(HyrameMessage.COMMAND_NOT_ENABLED.asString(player));

            event.setCancelled(true);
        }
    }

    private boolean isBlockedCommand(String command) {
        for (String blockedCommand : this.plugin.getHyrame().getCommandManager().getCommandBlocker().getBlockedCommands()) {
            if (command.equalsIgnoreCase(blockedCommand)) {
                return true;
            }
        }
        return false;
    }

}
