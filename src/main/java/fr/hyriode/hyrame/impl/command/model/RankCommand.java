package fr.hyriode.hyrame.impl.command.model;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.IHyriPlayerManager;
import fr.hyriode.api.rank.type.HyriPlayerRankType;
import fr.hyriode.api.rank.type.HyriStaffRankType;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 14:35
 */
public class RankCommand extends HyriCommand<HyramePlugin> {

    public RankCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("rank")
                .withDescription("Rank command")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/rank")
                .asynchronous()
                .withPermission(player -> player.getRank().is(HyriStaffRankType.ADMINISTRATOR)));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();
        final IHyriPlayerManager playerManager = HyriAPI.get().getPlayerManager();

        this.handleArgument(ctx, "player %player% %input%", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final UUID targetId = target.getUniqueId();

            if (player.getUniqueId().equals(targetId)) {
                return;
            }

            final HyriPlayerRankType rankType = this.getPlayerByName(output.get(String.class));

            if (rankType != null) {
                target.getRank().setPlayerType(rankType);

                playerManager.savePrefix(targetId, target.getNameWithRank());
                playerManager.updatePlayer(target);

                target.update();

                player.sendMessage(ChatColor.GREEN + "Grade joueur modifié!");
            } else {
                player.sendMessage(ChatColor.RED + "Grade joueur invalide!");
            }
        });

        this.handleArgument(ctx, "staff %player% reset", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final UUID targetId = target.getUniqueId();

            if (player.getUniqueId().equals(target.getUniqueId())) {
                return;
            }

            target.getRank().setStaffType(null);

            playerManager.savePrefix(targetId, target.getNameWithRank());
            playerManager.updatePlayer(target);

            target.update();

            player.sendMessage(ChatColor.GREEN + "Grade staff reset!");
        });

        this.handleArgument(ctx, "staff %player% %input%", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);

            if (player.getUniqueId().equals(target.getUniqueId())) {
                return;
            }

            final HyriStaffRankType rankType = this.getStaffByName(output.get(String.class));

            if (rankType != null) {
                target.getRank().setStaffType(rankType);
                playerManager.updatePlayer(target);
                target.update();

                player.sendMessage(ChatColor.GREEN + "Grade staff modifié!");
            } else {
                player.sendMessage(ChatColor.RED + "Grade staff invalide!");
            }
        });

        this.handleArgument(ctx, "player %player% reset", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final UUID targetId = target.getUniqueId();

            if (player.getUniqueId().equals(target.getUniqueId())) {
                return;
            }

            target.getRank().setPlayerType(null);

            playerManager.savePrefix(targetId, target.getNameWithRank());
            playerManager.updatePlayer(target);

            target.update();

            player.sendMessage(ChatColor.GREEN + "Grade joueur reset!");
        });
    }

    private HyriStaffRankType getStaffByName(String name) {
        for (HyriStaffRankType rankType : HyriStaffRankType.values()) {
            if (rankType.getName().equals(name)) {
                return rankType;
            }
        }
        return null;
    }

    private HyriPlayerRankType getPlayerByName(String name) {
        for (HyriPlayerRankType rankType : HyriPlayerRankType.values()) {
            if (rankType.getName().equals(name)) {
                return rankType;
            }
        }
        return null;
    }

}
