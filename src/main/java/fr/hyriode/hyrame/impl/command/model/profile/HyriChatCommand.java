package fr.hyriode.hyrame.impl.command.model.profile;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.chat.IHyriChatChannelManager;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.rank.EHyriRank;
import fr.hyriode.api.rank.HyriPermission;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HyriChatCommand extends HyriCommand<HyramePlugin> {

    public enum Permission implements HyriPermission {
        USE
    }

    public HyriChatCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("chat")
                .withDescription("Change your current chat, or send a message to the specified chat")
                .withUsage("/chat set <chat> | /chat <chat> <message>")
                .withType(HyriCommandType.PLAYER));

        Permission.USE.add(EHyriRank.PLAYER);
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();

        this.handleArgument(ctx, "set %input%", output -> {
            final String chat = output.get(String.class);

            if (HyriAPI.get().getChatChannelManager().getHandler(chat) == null) {
                player.sendMessage(ChatColor.RED + "Chat " + chat + " doesn't exist !");
                return;
            }

            final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());

            if (!IHyriChatChannelManager.canPlayerAccessChannel(chat, account)) {
                player.sendMessage(ChatColor.RED + "You can't join this chat !");
                return;
            }

            if (account.getSettings().getChatChannel().equals(chat)) {
                player.sendMessage(ChatColor.RED + "You are already in this chat !");
                return;
            }

            player.sendMessage(ChatColor.GREEN + "You are now talking in " + chat + " chat !");
            account.getSettings().setChatChannel(chat);
            account.update();
        });

        final IHyriChatChannelManager manager = HyriAPI.get().getChatChannelManager();

        this.handleArgument(ctx, "%input% %sentence%", output -> {
            final String chat = output.get(0, String.class);

            if (HyriAPI.get().getChatChannelManager().getHandler(chat) == null) {
                player.sendMessage(ChatColor.RED + "Chat " + chat + " doesn't exist !");
                return;
            }

            manager.sendMessage(chat, output.get(1, String.class), player.getUniqueId());
        });
    }
}
