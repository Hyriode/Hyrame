package fr.hyriode.hyrame.packet;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 06/05/2022 at 07:12
 */
public interface IPacketHandler {

    /**
     * Fired when a packet is received
     *
     * @param container The container of the packet
     */
    default void onReceive(IPacketContainer container) {}

    /**
     * Fired when a packet is sending
     *
     * @param container The container of the packet
     */
    default void onSend(IPacketContainer container) {}

}
