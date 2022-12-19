package fr.hyriode.hyrame.impl.packet;

import fr.hyriode.hyrame.packet.IPacketHandler;
import fr.hyriode.hyrame.packet.PacketType;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 06/05/2022 at 20:03
 */
public class PacketContext {

    private final PacketType type;
    private final IPacketHandler handler;

    public PacketContext(PacketType type, IPacketHandler handler) {
        this.type = type;
        this.handler = handler;
    }

    public PacketType getType() {
        return this.type;
    }

    public IPacketHandler getHandler() {
        return this.handler;
    }
}

