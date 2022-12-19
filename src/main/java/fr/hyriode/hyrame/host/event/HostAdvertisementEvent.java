package fr.hyriode.hyrame.host.event;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEvent;
import fr.hyriode.api.host.HostData;
import fr.hyriode.hyggdrasil.api.server.HyggServer;

/**
 * Created by AstFaster
 * on 03/08/2022 at 22:05
 */
public class HostAdvertisementEvent extends HyriEvent {

    private final String serverName;

    public HostAdvertisementEvent(String serverName) {
        this.serverName = serverName;
    }

    public String getServerName() {
        return this.serverName;
    }

    public HostData getHostData() {
        final HyggServer server = HyriAPI.get().getServerManager().getServer(this.serverName);

        return server == null ? null : HyriAPI.get().getHostManager().getHostData(server);
    }

}
