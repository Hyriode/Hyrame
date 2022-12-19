package fr.hyriode.hyrame.impl.chat;

import fr.hyriode.hyrame.chat.IHyriChatHandler;
import fr.hyriode.hyrame.chat.IChatManager;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.impl.game.chat.GameChatHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 20/04/2022 at 18:54
 */
public class ChatManager implements IChatManager {

    private final TreeMap<Integer, IHyriChatHandler> handlers;

    public ChatManager(Hyrame hyrame) {
        this.handlers = new TreeMap<>();

        this.registerHandler(50, new GameChatHandler(hyrame));
    }

    void onEvent(AsyncPlayerChatEvent event) {
        for (IHyriChatHandler handler : this.handlers.values()) {
            if (!handler.onChat(event)) {
                return;
            }
        }
    }

    @Override
    public void registerHandler(int priority, IHyriChatHandler handler) {
        this.handlers.put(priority, handler);
    }

    @Override
    public void unregisterHandler(IHyriChatHandler handler) {
        for (Map.Entry<Integer, IHyriChatHandler> entry : this.handlers.entrySet()) {
            if (entry.getValue().equals(handler)) {
                this.handlers.remove(entry.getKey());
            }
        }
    }

    @Override
    public List<IHyriChatHandler> getHandlers() {
        return new ArrayList<>(this.handlers.values());
    }

}
