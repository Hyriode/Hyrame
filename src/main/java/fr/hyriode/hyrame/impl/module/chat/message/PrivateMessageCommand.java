package fr.hyriode.hyrame.impl.module.chat.message;

import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 14:24
 */
public class PrivateMessageCommand extends HyriCommand<HyramePlugin> {

    public PrivateMessageCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("msg")
                .withAliases("message", "m", "dm", "pm", "tell")
                .withDescription("The command used to create a party and interact with it")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/msg <player> <message>"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();

        this.handleArgument(ctx, "%player_online% %sentence%", output -> this.plugin.getHyrame().getPrivateMessageModule().sendPrivateMessage(player, output.get(IHyriPlayer.class), output.get(String.class)));
    }

}
