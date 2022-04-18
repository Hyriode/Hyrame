package fr.hyriode.hyrame.impl.command.model.server;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.rank.EHyriRank;
import fr.hyriode.api.rank.HyriPermission;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import org.bukkit.entity.Player;

public class HyriServerCommand extends HyriCommand<HyramePlugin> {

    private enum Permission implements HyriPermission {
        USE
    }

    public HyriServerCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("hyriserver")
                .withUsage("/hyriserver <server>")
                .withPermission(Permission.USE)
                .withType(HyriCommandType.PLAYER));

        Permission.USE.add(EHyriRank.STAFF);
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        this.handleArgument(ctx, "%input%", output -> HyriAPI.get().getServerManager().sendPlayerToServer(((Player) ctx.getSender()).getUniqueId(), output.get(String.class)));
    }
}
