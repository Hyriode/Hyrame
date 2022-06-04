package fr.hyriode.hyrame.config.handler;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.config.ConfigProcess;
import fr.hyriode.hyrame.utils.LocationWrapper;

/**
 * Created by AstFaster
 * on 01/06/2022 at 13:36
 */
public class CLocationHandler extends ConfigOptionHandler<LocationWrapper> {

    public static final String SET_ITEM = "config_location_set";
    public static final String EYES_ITEM = "config_location_eyes";

    private boolean eyes = true;

    public CLocationHandler(IHyrame hyrame, ConfigProcess<?> process) {
        super(hyrame, process);
    }

    @Override
    public void handle() {
        this.hyrame.getItemManager().giveItem(this.player, 0, SET_ITEM);
        this.hyrame.getItemManager().giveItem(this.player, 1, EYES_ITEM);
    }

    public void provideLocation(LocationWrapper location) {
        if (!this.eyes) {
            location.setYaw(0.0F);
            location.setPitch(0.0F);
        }

        this.player.getInventory().clear();

        this.complete(location);
    }

    public void setEyes(boolean eyes) {
        this.eyes = eyes;

        this.hyrame.getItemManager().giveItem(this.player, 1, EYES_ITEM);
    }

    public boolean isEyes() {
        return this.eyes;
    }

}
