package fr.hyriode.hyrame.host.event;

import fr.hyriode.api.event.HyriEvent;
import fr.hyriode.hylios.api.host.HostData;

/**
 * Created by AstFaster
 * on 03/08/2022 at 22:05
 */
public class HostAdvertisementEvent extends HyriEvent {

    private final HostData hostData;

    public HostAdvertisementEvent(HostData hostData) {
        this.hostData = hostData;
    }

    public HostData getHostData() {
        return this.hostData;
    }

}
