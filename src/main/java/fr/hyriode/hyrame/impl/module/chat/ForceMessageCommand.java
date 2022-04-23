package fr.hyriode.hyrame.impl.module.chat;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceMessageCommand extends HyriCommand<HyramePlugin> {

    public ForceMessageCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("forcemsg")
                .withDescription("Force a message to a chat")
                .withUsage("/forcemsg <chat> <message>")
                .withPermission(player -> player.getRank().isStaff())
                .withType(HyriCommandType.PLAYER));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();

        this.handleArgument(ctx, "%input% %sentence%", output -> {
            final String chat = output.get(0, String.class);

            if (HyriAPI.get().getChatChannelManager().getHandler(chat) == null) {
                player.sendMessage(ChatColor.RED + ChatCommand.MESSAGE.apply(player, "invalid"));
                return;
            }

            HyriAPI.get().getChatChannelManager().sendMessage(chat, output.get(1, String.class), player.getUniqueId(), true);
        });
    }
}
