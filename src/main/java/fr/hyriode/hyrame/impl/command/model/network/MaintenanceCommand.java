package fr.hyriode.hyrame.impl.command.model.network;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.network.IHyriNetwork;
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
public class MaintenanceCommand extends HyriCommand<HyramePlugin> {

    public MaintenanceCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("maintenance")
                .withDescription("Maintenance command")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/maintenance <on|off>")
                .withPermission(player -> player.getRank().is(HyriStaffRankType.ADMINISTRATOR)));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();

        this.handleArgument(ctx, "on", output -> {
            final IHyriNetwork network = HyriAPI.get().getNetworkManager().getNetwork();

            network.getMaintenance().enable(player.getUniqueId(), null);
            network.update();

            player.sendMessage(ChatColor.GREEN + "Maintenance activé.");
        });
        this.handleArgument(ctx, "off", output -> {
            final IHyriNetwork network = HyriAPI.get().getNetworkManager().getNetwork();

            network.getMaintenance().disable();
            network.update();

            player.sendMessage(ChatColor.RED + "Maintenance désactivé.");
        });
    }

}
