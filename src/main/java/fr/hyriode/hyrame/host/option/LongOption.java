package fr.hyriode.hyrame.host.option;

import fr.hyriode.hyrame.host.HostDisplay;
import fr.hyriode.hyrame.language.HyrameMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Created by AstFaster
 * on 31/07/2022 at 11:15
 */
public class LongOption extends HostOption<Long> {

    protected final long minimum;
    protected final long maximum;

    public LongOption(HostDisplay display, long defaultValue, long minimum, long maximum) {
        super(display, defaultValue);
        this.minimum = minimum;
        this.maximum = maximum;
        this.valueFormatter = player -> HyrameMessage.HOST_OPTION_NUMBER_FORMATTER.asString(player).replace("%value%", String.valueOf(this.value));
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        if (event.isLeftClick()) {
            this.setValue(this.value + 1);
        } else if (event.isRightClick()) {
            this.setValue(this.value - 1);
        }
    }

    @Override
    public void setValue(Long value) {
        if (value < this.minimum) {
            value = this.minimum;
        }

        if (value > this.maximum) {
            value = this.maximum;
        }

        super.setValue(value);
    }

}
