package fr.hyriode.hyrame.impl.command.model;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HyriServerCommand extends HyriCommand<HyramePlugin> {

    public HyriServerCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("hyriserver")
                .withDescription("Send you to a given server")
                .withUsage("/hyriserver <server>")
                .withPermission(player -> player.getRank().isStaff())
                .withType(HyriCommandType.PLAYER));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        this.handleArgument(ctx, "%input%", output -> {
            final String server = output.get(String.class);
            final Player player = (Player) ctx.getSender();

            player.sendMessage(ChatColor.GREEN + HyriLanguageMessage.get("message.join.sending").getForPlayer(player).replace("%server%", server));

            HyriAPI.get().getServerManager().sendPlayerToServer(player.getUniqueId(), server);
        });
    }
}
