package fr.hyriode.hyrame.packet;

import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 06/05/2022 at 07:12
 */
public interface IPacketInterceptor {

    void addHandler(PacketType packetType, IPacketHandler handler);

    default void addHandler(IPacketHandler handler) {
        this.addHandler(PacketType.ALL, handler);
    }

    void removeHandler(IPacketHandler handler);

    List<IPacketHandler> getHandlers();

    List<IPacketHandler> getHandlers(PacketType packetType);

}
