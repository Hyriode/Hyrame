package fr.hyriode.hyrame.config.handler;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.config.ConfigProcess;

/**
 * Created by AstFaster
 * on 03/06/2022 at 11:04
 */
public class CBooleanHandler extends ConfigOptionHandler<Boolean> {

    public static final String YES_ITEM = "config_boolean_yes";
    public static final String NO_ITEM = "config_boolean_no";

    public CBooleanHandler(IHyrame hyrame, ConfigProcess<?> process) {
        super(hyrame, process);
    }

    @Override
    public void handle() {
        this.hyrame.getItemManager().giveItem(this.player, 0, YES_ITEM);
        this.hyrame.getItemManager().giveItem(this.player, 1, NO_ITEM);
    }

    public void validate(boolean value) {
        this.player.getInventory().clear();

        this.complete(value);
    }

}
