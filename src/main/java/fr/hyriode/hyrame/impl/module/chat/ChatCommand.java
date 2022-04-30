package fr.hyriode.hyrame.impl.module.chat;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.chat.channel.IHyriChatChannelManager;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.chat.HyriMessageEvent;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.BiFunction;

public class ChatCommand extends HyriCommand<HyramePlugin> {

    public static final BiFunction<Player, String, String> MESSAGE = (player, key) -> HyriLanguageMessage.get("message.chat." + key).getForPlayer(player);

    public ChatCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("chat")
                .withDescription("Change your current chat, or send a message to the specified chat")
                .withUsage("/chat set <chat> | /chat <chat> <message>")
                .withType(HyriCommandType.PLAYER));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();
        final UUID playerId = player.getUniqueId();

        this.handleArgument(ctx, "set %input%", output -> {
            final String chat = output.get(String.class).toLowerCase();

            if (HyriAPI.get().getChatChannelManager().getHandler(chat) == null) {
                player.sendMessage(ChatColor.RED + MESSAGE.apply(player, "invalid"));
                return;
            }

            final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(playerId);

            if (!IHyriChatChannelManager.canPlayerAccessChannel(chat, account)) {
                player.sendMessage(ChatColor.RED + MESSAGE.apply(player, "cant-join"));
                return;
            }

            if (account.getSettings().getChatChannel().equals(chat)) {
                player.sendMessage(ChatColor.RED + MESSAGE.apply(player, "already-in"));
                return;
            }

            player.sendMessage(ChatColor.GREEN + MESSAGE.apply(player, "now-talking").replace("%chat%", chat));

            account.getSettings().setChatChannel(chat);
            account.update();
        });

        final IHyriChatChannelManager manager = HyriAPI.get().getChatChannelManager();

        this.handleArgument(ctx, "%input% %sentence%", output -> {
            final String chat = output.get(0, String.class).toLowerCase();

            if (HyriAPI.get().getChatChannelManager().getHandler(chat) == null) {
                player.sendMessage(ChatColor.RED + MESSAGE.apply(player, "invalid"));
                return;
            }

            final String message = output.get(1, String.class);

            final HyriMessageEvent event = new HyriMessageEvent(playerId, message);

            HyriAPI.get().getEventBus().publish(event);

            if (!event.isCancelled()) {
                manager.sendMessage(chat, message, playerId);
            }
        });
    }
}
