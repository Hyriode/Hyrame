package fr.hyriode.hyrame.impl.command.model;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyggdrasil.api.lobby.HyggLobbyAPI;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 14:35
 */
public class LobbyCommand extends HyriCommand<HyramePlugin> {

    public LobbyCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("lobby")
                .withAliases("l", "hub")
                .withDescription("The command used to return to lobby")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/lobby"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();

        if (!HyriAPI.get().getServer().getType().equalsIgnoreCase(HyggLobbyAPI.TYPE)) {
            HyriAPI.get().getServerManager().sendPlayerToLobby(player.getUniqueId());

            player.sendMessage(HyriLanguageMessage.get("message.lobby.sending").getForPlayer(player));
        } else {
            player.sendMessage(HyriLanguageMessage.get("message.lobby.already-in").getForPlayer(player));
        }
    }

}
