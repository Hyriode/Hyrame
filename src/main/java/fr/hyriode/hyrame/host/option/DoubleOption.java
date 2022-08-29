package fr.hyriode.hyrame.host.option;

import fr.hyriode.hyrame.host.HostDisplay;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyrameMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by AstFaster
 * on 28/08/2022 at 09:33
 */
public class DoubleOption extends HostOption<Double> {

    protected final double minimum;
    protected final double maximum;

    public DoubleOption(HostDisplay display, double defaultValue, double minimum, double maximum) {
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
    public ItemStack createItem(Player player) {
        final ItemBuilder builder = new ItemBuilder(super.createItem(player));
        final List<String> lore = builder.getLore();

        if (this.getClass() == DoubleOption.class) {
            lore.set(lore.size() - 1, HyrameMessage.HOST_CLICK_TO_INCREASE.asString(player));
            lore.add(HyrameMessage.HOST_CLICK_TO_DECREASE.asString(player));
        }

        return builder.withLore(lore).build();
    }

    @Override
    public void setValue(Double value) {
        if (value < this.minimum) {
            value = this.minimum;
        }

        if (value > this.maximum) {
            value = this.maximum;
        }

        super.setValue(value);
    }

    public double getMaximum() {
        return this.maximum;
    }

    public double getMinimum() {
        return this.minimum;
    }

}
