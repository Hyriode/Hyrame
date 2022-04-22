package fr.hyriode.hyrame.chat;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.chat.HyriChatChannel;
import fr.hyriode.api.chat.IHyriChatChannelManager;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class HyriDefaultChatHandler implements IHyriChatHandler {

    private boolean cancelled;

    @Override
    public boolean onChat(AsyncPlayerChatEvent event) {
        if (this.cancelled) {
            return true;
        }

        event.setCancelled(true);

        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(uuid);

        if (account == null) {
            return false;
        }

        final String channel = account.getSettings().getChatChannel();

        if (!IHyriChatChannelManager.canPlayerAccessChannel(channel, account)) {
            player.sendMessage(ChatColor.RED + HyriLanguageMessage.get("message.error.chat.cant-talk").getForPlayer(player));
            account.getSettings().setChatChannel(HyriChatChannel.GLOBAL.getChannel());
            account.update();
        }

        HyriAPI.get().getChatChannelManager().sendMessage(channel, event.getMessage(), uuid);
        return true;
    }

    @Override
    public String format() {
        return null;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
