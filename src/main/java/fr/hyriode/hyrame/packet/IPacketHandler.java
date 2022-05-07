package fr.hyriode.hyrame.packet;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 06/05/2022 at 07:12
 */
public interface IPacketHandler {

    default void onReceive(IPacketContainer container) {}

    default void onSend(IPacketContainer container) {}

}
