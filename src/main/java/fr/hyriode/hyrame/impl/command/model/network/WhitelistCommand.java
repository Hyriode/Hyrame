package fr.hyriode.hyrame.impl.command.model.network;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.rank.type.HyriStaffRankType;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 14:35
 */
public class WhitelistCommand extends HyriCommand<HyramePlugin> {

    public WhitelistCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("whitelist")
                .withDescription("Whitelist command")
                .withAliases("wl")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/whitelist add <player>")
                .withPermission(player -> player.getRank().is(HyriStaffRankType.ADMINISTRATOR)));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();

        this.handleArgument(ctx, "add %input%", output -> {
            final String playerName = output.get(String.class);

            if (HyriAPI.get().getPlayerManager().getWhitelistManager().isWhitelisted(playerName)) {
                player.sendMessage(ChatColor.RED + "Ce joueur est déjà ajouté dans la whitelist.");
                return;
            }

            HyriAPI.get().getPlayerManager().getWhitelistManager().whitelistPlayer(playerName);

            player.sendMessage(ChatColor.GREEN + "Joueur ajouté dans la whitelist.");
        });
    }

}
