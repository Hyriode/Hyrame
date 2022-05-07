package fr.hyriode.hyrame.impl.command.model.network;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.rank.type.HyriStaffRankType;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.utils.SerializerUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 01/05/2022 at 20:10
 */
public class BroadcastMessage extends HyriCommand<HyramePlugin> {

    public BroadcastMessage(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("say")
                .withDescription("The command used to vanish yourself from other players")
                .withAliases("bc", "broadcast", "bc", "annonce")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/vanish")
                .withPermission(player -> player.getRank().is(HyriStaffRankType.ADMINISTRATOR)));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();

        this.handleArgument(ctx, "%sentence%", output -> {
            final ComponentBuilder builder = new ComponentBuilder("\nAnnonce ▕ ").color(ChatColor.DARK_AQUA).
                    append(output.get(String.class)).color(ChatColor.WHITE).append("\n");

            HyriAPI.get().getServerManager().broadcastMessage(player.getUniqueId(), SerializerUtil.serializeComponent(builder.create()));
        });
    }

}
