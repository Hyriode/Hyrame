package fr.hyriode.hyrame.impl.command.model;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.server.reconnection.IHyriReconnectionData;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 26/05/2022 at 19:33
 */
public class RejoinCommand extends HyriCommand<HyramePlugin> {

    public RejoinCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("rejoin")
                .withAliases("reconnect")
                .withDescription("Command used to rejoin a left game")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/rejoin"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();
        final IHyriReconnectionData reconnectionData = HyriAPI.get().getServerManager().getReconnectionHandler().get(player.getUniqueId());

        if (reconnectionData != null) {
            player.sendMessage(HyriLanguageMessage.get("message.rejoin.processing").getForPlayer(player));

            reconnectionData.reconnect();
        } else {
            player.sendMessage(HyriLanguageMessage.get("message.rejoin.cancel").getForPlayer(player));
        }
    }

}
