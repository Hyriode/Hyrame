package fr.hyriode.hyrame.impl.command.model.profile;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.IHyriPlayerManager;
import fr.hyriode.api.rank.EHyriRank;
import fr.hyriode.api.settings.HyriLanguage;
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
 * on 12/12/2021 at 20:10
 */
public class HyriLanguageCommand extends HyriCommand<HyramePlugin> {

    public HyriLanguageCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("language")
                .withDescription("Change your current language")
                .withUsage("/language [code]")
                .withType(HyriCommandType.PLAYER));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        this.handleArgument(ctx, "%input%", output -> {
            final String code = output.get(String.class);
            final HyriLanguage language = HyriLanguage.getByCode(code);
            final Player player = (Player) ctx.getSender();
            final IHyriPlayerManager playerManager = HyriAPI.get().getPlayerManager();
            final IHyriPlayer account = playerManager.getPlayer(player.getUniqueId());

            if (language != null) {
                account.getSettings().setLanguage(language);

                playerManager.sendPlayer(account);

                plugin.getHyrame().getLanguageManager().updatePlayerLanguage(player);

                player.sendMessage(ChatColor.GREEN + "Your language has been updated.");
            } else {
                player.sendMessage(ChatColor.RED + "Invalid language!");

                final String newLine = "\n";
                final StringBuilder builder = new StringBuilder("Available languages: ").append(newLine);

                for (HyriLanguage l : HyriLanguage.values()) {
                    builder.append("- ").append(l.getCode()).append(newLine);
                }

                player.sendMessage(ChatColor.RED + builder.toString());
            }
        });
    }

}
