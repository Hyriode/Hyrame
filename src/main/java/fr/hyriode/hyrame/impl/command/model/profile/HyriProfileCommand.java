package fr.hyriode.hyrame.impl.command.model.profile;

import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyriapi.player.IHyriPlayer;
import fr.hyriode.hyriapi.rank.EHyriRank;
import fr.hyriode.hyriapi.rank.HyriPermission;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 04/12/2021 at 13:07
 */
public class HyriProfileCommand extends HyriCommand<HyramePlugin> {

    private enum Permission implements HyriPermission {
        USE, INFO, EDIT
    }

    public HyriProfileCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("hyriprofile")
                .withDescription("Manage and check a player profile")
                .withUsage("/hyriprofile [player]")
                .withType(HyriCommandType.PLAYER)
                .withPermission(Permission.USE));

        Permission.USE.add(EHyriRank.ADMINISTRATOR);
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();

        this.handleArgument(ctx, "%player%", output -> {
            final IHyriPlayer account = output.get(IHyriPlayer.class);

            player.sendMessage(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(account.getLastLoginDate()));
            player.sendMessage(account.getName());
        });

    }

}
