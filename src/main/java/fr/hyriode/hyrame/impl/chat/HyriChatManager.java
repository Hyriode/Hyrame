package fr.hyriode.hyrame.impl.chat;

import fr.hyriode.hyrame.chat.HyriDefaultChatHandler;
import fr.hyriode.hyrame.chat.IHyriChatHandler;
import fr.hyriode.hyrame.chat.IHyriChatManager;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.impl.game.chat.HyriGameChatHandler;
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
public class HyriChatManager implements IHyriChatManager {

    private final TreeMap<Integer, IHyriChatHandler> handlers;

    public HyriChatManager(Hyrame hyrame) {
        this.handlers = new TreeMap<>();

        this.registerHandler(100, new HyriDefaultChatHandler());
        this.registerHandler(99, new HyriGameChatHandler(hyrame));
    }

    void onEvent(AsyncPlayerChatEvent event) {
        for (IHyriChatHandler handler : this.handlers.values()) {
            final boolean continueProcess = handler.onChat(event);

            if (!continueProcess) {
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
