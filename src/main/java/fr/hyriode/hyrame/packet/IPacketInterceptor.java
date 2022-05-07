package fr.hyriode.hyrame.packet;

import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 06/05/2022 at 07:12
 */
public interface IPacketInterceptor {

    /**
     * Add a packet handler for a given {@link PacketType}
     *
     * @param packetType The type of the packet
     * @param handler The handler to register
     */
    void addHandler(PacketType packetType, IPacketHandler handler);

    /**
     * Add a packet handler
     *
     * @param handler The handler to register
     */
    default void addHandler(IPacketHandler handler) {
        this.addHandler(PacketType.ALL, handler);
    }

    /**
     * Remove a packet handler
     *
     * @param handler The handler to remove
     */
    void removeHandler(IPacketHandler handler);

    /**
     * Get all the existing handlers
     *
     * @return A list of {@link IPacketHandler}
     */
    List<IPacketHandler> getHandlers();

    /**
     * Get all the existing handlers with a given type
     *
     * @param packetType The type of packet linked to the handlers
     * @return A list of {@link IPacketHandler}
     */
    List<IPacketHandler> getHandlers(PacketType packetType);

}
