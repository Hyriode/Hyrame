package fr.hyriode.hyrame.chat;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.chat.HyriDefaultChatChannel;
import fr.hyriode.api.chat.IHyriChatChannelManager;
import fr.hyriode.api.player.IHyriPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class HyriDefaultChatHandler implements IHyriChatHandler {

    private boolean cancelled;

    @Override
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        if (this.isCancelled()) {
            return;
        }

        final UUID uuid = event.getPlayer().getUniqueId();
        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(uuid);

        if (account == null) {
            return;
        }

        if (!IHyriChatChannelManager.canPlayerAccessChannel(account.getSettings().getChatChannel(), account)) {
            event.getPlayer().sendMessage(ChatColor.RED + "You can't talk in this channel, transferring to default channel...");
            account.getSettings().setChatChannel(HyriDefaultChatChannel.GLOBAL.getChannel());
            account.update();
        }

        HyriAPI.get().getChatChannelManager().sendMessage(account.getSettings().getChatChannel(), event.getMessage(), uuid);
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
