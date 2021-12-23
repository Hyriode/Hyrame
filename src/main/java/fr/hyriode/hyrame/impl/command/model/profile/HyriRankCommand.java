package fr.hyriode.hyrame.impl.command.model.profile;

import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.hyriapi.player.IHyriPlayer;
import fr.hyriode.hyriapi.player.IHyriPlayerManager;
import fr.hyriode.hyriapi.rank.HyriRank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/12/2021 at 20:10
 */
public class HyriRankCommand extends HyriCommand<HyramePlugin> {

    public HyriRankCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("rank")
                .withDescription("Change your current rank")
                .withUsage("/rank [name]")
                .withType(HyriCommandType.PLAYER));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        this.handleArgument(ctx, "%input%", output -> {
            final Player player = (Player) ctx.getSender();
            final IHyriPlayerManager playerManager = HyriAPI.get().getPlayerManager();
            final IHyriPlayer account = playerManager.getPlayer(player.getUniqueId());
            final HyriRank rank = HyriAPI.get().getRankManager().getRank(output.get(String.class));

            if (rank != null) {
                account.setRank(rank);

                playerManager.sendPlayer(account);

                plugin.getHyrame().getTabManager().onLogout(player);
                plugin.getHyrame().getTabManager().onLogin(player);

                player.sendMessage(ChatColor.GREEN + "Your rank has been updated.");
            } else {
                player.sendMessage(ChatColor.RED + "Invalid rank!");
            }
        });
    }

}
