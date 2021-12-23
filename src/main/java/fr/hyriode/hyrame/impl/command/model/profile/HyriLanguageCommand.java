package fr.hyriode.hyrame.impl.command.model.profile;

import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.hyriapi.player.IHyriPlayer;
import fr.hyriode.hyriapi.player.IHyriPlayerManager;
import fr.hyriode.hyriapi.settings.HyriLanguage;
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
            }
        });
    }

}
