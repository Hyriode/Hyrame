package fr.hyriode.hyrame.impl.command.model.other;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.rank.EHyriRank;
import fr.hyriode.api.rank.HyriPermission;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HyriSpecialCommand extends HyriCommand<HyramePlugin> {

    public enum Permission implements HyriPermission {
        USE
    }

    public HyriSpecialCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("special")
                .withDescription("Special commands for staff")
                .withUsage("/special <action>")
                .withType(HyriCommandType.PLAYER));

        Permission.USE.add(EHyriRank.STAFF);
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();

        this.handleArgument(ctx, "chat %input% %sentence%", output -> {
            final String chat = output.get(0, String.class);

            if (HyriAPI.get().getChatChannelManager().getHandler(chat) == null) {
                player.sendMessage(ChatColor.RED + "Chat " + chat + " doesn't exist !");
                return;
            }

            HyriAPI.get().getChatChannelManager().sendMessage(chat, output.get(1, String.class), player.getUniqueId(), true);
        });
    }
}
