package fr.hyriode.hyrame.impl.module.friend;

import fr.hyriode.api.friend.HyriFriendRequest;
import fr.hyriode.api.packet.HyriPacket;
import fr.hyriode.api.packet.IHyriPacketReceiver;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/04/2022 at 08:45
 */
public class FriendReceiver implements IHyriPacketReceiver {

    private final FriendModule friendModule;

    public FriendReceiver(FriendModule friendModule) {
        this.friendModule = friendModule;
    }

    @Override
    public void receive(String channel, HyriPacket packet) {
        if (packet instanceof HyriFriendRequest.Packet) {
            this.friendModule.onRequest(((HyriFriendRequest.Packet) packet).getRequest());
        }
    }

}
