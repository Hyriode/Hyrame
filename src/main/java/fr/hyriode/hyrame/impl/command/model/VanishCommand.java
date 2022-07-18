package fr.hyriode.hyrame.impl.command.model;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.rank.type.HyriPlayerRankType;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.utils.PlayerUtil;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 14:35
 */
public class VanishCommand extends HyriCommand<HyramePlugin> {

    public VanishCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("vanish")
                .withDescription("The command used to vanish yourself from other players")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/vanish")
                .withPermission(player -> player.getRank().isStaff() || player.getRank().is(HyriPlayerRankType.PARTNER)));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();
        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());
        final HyriGame<?> currentGame = this.plugin.getHyrame().getGameManager().getCurrentGame();

        if (currentGame != null) {
            player.sendMessage(HyriLanguageMessage.get("message.vanish.game").getValue(account));
            return;
        }

        if (account.isInVanishMode()) {
            account.setInVanishMode(false);
            PlayerUtil.showPlayer(player);

            player.sendMessage(HyriLanguageMessage.get("message.vanish.unset").getValue(account));
        } else {
            account.setInVanishMode(true);
            PlayerUtil.hidePlayer(player, true);

            player.sendMessage(HyriLanguageMessage.get("message.vanish.set").getValue(account));
        }

        account.update();
    }

}
