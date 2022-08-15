package fr.hyriode.hyrame.host.option;

import fr.hyriode.hyrame.host.HostDisplay;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.item.ItemHead;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.utils.HyrameHead;
import fr.hyriode.hyrame.utils.list.ListReplacer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by AstFaster
 * on 31/07/2022 at 11:15
 */
public class PreciseIntegerOption extends IntegerOption {

    private final int[] modifiers;

    public PreciseIntegerOption(HostDisplay display, int defaultValue, int minimum, int maximum, int[] modifiers) {
        super(display, defaultValue, minimum, maximum);
        this.modifiers = modifiers;

        if (this.modifiers.length < 2) {
            throw new IllegalArgumentException("Modifiers need to be at least 2");
        }
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        new GUI(player).open();
    }

    private class GUI extends HyriInventory {

        public GUI(Player owner) {
            super(owner, ChatColor.stripColor(displayName.getValue(owner)), 6 * 9);

            this.setItem(49, new ItemBuilder(Material.ARROW)
                            .withName(HyrameMessage.GO_BACK.asString(this.owner))
                            .build(),
                    event -> categoryGUIProvider.apply(this.owner).open());

            this.setItem(31, ItemBuilder.asHead(HyrameHead.GARBAGE_CAN)
                    .withName(HyrameMessage.HOST_RESET_NAME.asString(this.owner))
                    .withLore(HyrameMessage.HOST_RESET_LORE.asList(this.owner))
                    .build(),
                    event -> {
                        setValue(defaultValue);

                        this.addItems();
                    });

            this.addItems();
        }

        private void addItems() {
            // Minus
            this.createItem(30, 0, false, HyrameHead.WHITE_MINUS, ChatColor.AQUA);
            this.createItem(29, 1, false, HyrameHead.GRAY_MINUS, ChatColor.DARK_AQUA);

            // Plus
            this.createItem(32, 0, true, HyrameHead.WHITE_PLUS, ChatColor.AQUA);
            this.createItem(33, 1, true, HyrameHead.GRAY_PLUS, ChatColor.DARK_AQUA);
        }

        private void createItem(int slot, int index, boolean plus, ItemHead head, ChatColor color) {
            final int modifier = modifiers[index];
            final ItemStack itemStack = ItemBuilder.asHead(head)
                    .withName(color + (plus ? "+" : "-") + modifier)
                    .withLore(ListReplacer.replace(HyrameMessage.HOST_MULTIPLE_MODIFIERS_LORE.asList(this.owner), "%value%", String.valueOf(value))
                            .replace("%maximum%", String.valueOf(maximum))
                            .replace("%minimum%", String.valueOf(minimum))
                            .list())
                    .build();

            this.setItem(slot, itemStack, event -> {
                setValue(plus ? value + modifier : value - modifier);

                this.addItems();
            });
        }

    }

}
