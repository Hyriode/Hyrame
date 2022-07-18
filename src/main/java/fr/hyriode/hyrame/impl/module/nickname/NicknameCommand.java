package fr.hyriode.hyrame.impl.module.nickname;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.nickname.IHyriNickname;
import fr.hyriode.api.rank.HyriRank;
import fr.hyriode.api.rank.type.HyriPlayerRankType;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.language.HyriCommonMessages;
import fr.hyriode.hyrame.utils.ThreadUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 21:14
 */
public class NicknameCommand extends HyriCommand<HyramePlugin> {

    private final NicknameModule nicknameModule;

    public NicknameCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("nick")
                .withAliases("disguise")
                .withDescription("The command used to edit profile name, skin, etc.")
                .withType(HyriCommandType.PLAYER)
                .withPermission(player -> player.hasHyriPlus() || player.getRank().isStaff() || player.getRank().is(HyriPlayerRankType.PARTNER))
                .withUsage("/nick [custom|reset]"));
        this.nicknameModule = this.plugin.getHyrame().getNicknameModule();
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();
        final HyriGame<?> currentGame = this.plugin.getHyrame().getGameManager().getCurrentGame();

        if (currentGame != null) {
            player.sendMessage(HyriLanguageMessage.get("message.nickname.game").getValue(player));
            return;
        }

        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());
        final HyriRank rank = account.getRank();
        final IHyriNickname currentNickname = account.getNickname();

        if (ctx.getArgs().length == 0) {
            ThreadUtil.ASYNC_EXECUTOR.execute(() -> this.nicknameModule.processNickname(player));
            return;
        }

        this.handleArgument(ctx, "custom", output -> {
            if (rank.isStaff() || rank.is(HyriPlayerRankType.PARTNER)) {
                if (currentNickname != null) {
                    new NicknameGUI(player, this.nicknameModule, currentNickname.getName(), currentNickname.getSkinOwner(), currentNickname.getRank()).open();
                } else {
                    new NicknameGUI(player, this.nicknameModule, null, HyriPlayerRankType.PLAYER).open();
                }
            } else {
                player.sendMessage(ChatColor.RED + HyriCommonMessages.DONT_HAVE_PERMISSION.getValue(player));
            }
        });
        this.handleArgument(ctx, "reset", output -> {
            if (currentNickname != null) {
                this.nicknameModule.resetNickname(player);

                player.sendMessage(HyriLanguageMessage.get("message.nickname.remove-nick").getValue(player));
            } else {
                player.sendMessage(HyriLanguageMessage.get("message.nickname.not-nick").getValue(player));
            }
        });
    }

}
