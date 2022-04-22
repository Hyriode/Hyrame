package fr.hyriode.hyrame.chat;

import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 20/04/2022 at 18:51
 */
public interface IHyriChatManager {

    /**
     * Register a chat handler with a given priority
     *
     * @param priority The priority of the handler
     * @param handler The handler to register
     */
    void registerHandler(int priority, IHyriChatHandler handler);

    /**
     * Unregister a given handler
     *
     * @param handler The handler to unregister
     */
    void unregisterHandler(IHyriChatHandler handler);

    /**
     * Get all the registered chat handlers
     *
     * @return A list of {@link IHyriChatHandler}
     */
    List<IHyriChatHandler> getHandlers();

}
