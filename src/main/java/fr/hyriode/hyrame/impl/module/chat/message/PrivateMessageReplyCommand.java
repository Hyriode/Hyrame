package fr.hyriode.hyrame.impl.module.chat.message;

import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 15:52
 */
public class PrivateMessageReplyCommand extends HyriCommand<HyramePlugin> {

    public PrivateMessageReplyCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("reply")
                .withAliases("r")
                .withDescription("The command used to reply to your latest message")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/r <message>"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();

        this.handleArgument(ctx, "%sentence%", output -> PrivateMessageUtil.replyToMessage(player, output.get(String.class)));
    }

}
