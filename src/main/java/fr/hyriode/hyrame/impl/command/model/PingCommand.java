package fr.hyriode.hyrame.impl.command.model;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 14:35
 */
public class PingCommand extends HyriCommand<HyramePlugin> {

    public PingCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("ping")
                .withAliases("lag", "latence", "ms")
                .withDescription("The command used to vanish yourself from other players")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/ping"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();

        player.sendMessage(HyriLanguageMessage.get("message.ping").getValue(player).replace("%ping%", String.valueOf(HyriAPI.get().getPlayerManager().getPing(player.getUniqueId()))));
    }

}
