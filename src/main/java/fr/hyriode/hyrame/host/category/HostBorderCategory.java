package fr.hyriode.hyrame.host.category;

import fr.hyriode.hyrame.host.HostCategory;
import fr.hyriode.hyrame.host.HostDisplay;
import fr.hyriode.hyrame.host.gui.HostGUI;
import fr.hyriode.hyrame.host.option.DoubleOption;
import fr.hyriode.hyrame.host.option.PreciseIntegerOption;
import fr.hyriode.hyrame.host.option.TimeOption;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.item.ItemHead;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.utils.HyrameHead;
import fr.hyriode.hyrame.utils.TimeUtil;
import fr.hyriode.hyrame.utils.UsefulDisplay;
import fr.hyriode.hyrame.utils.list.ListReplacer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by AstFaster
 * on 26/08/2022 at 08:55
 */
public class HostBorderCategory extends HostCategory {

    private SizeOption initialSizeOption;
    private SizeOption finalSizeOption;
    private SpeedOption speedOption;
    private CustomTimeOption timeOption;

    public HostBorderCategory(HostDisplay display) {
        super(display);
        this.guiProvider = player -> new GUI(player, this);
    }

    public HostBorderCategory addInitialSizeOption(String optionName, int defaultValue, int minimum, int maximum) {
        this.addOption(9, this.initialSizeOption = new SizeOption(UsefulDisplay.optionDisplay(optionName, "border-initial-size", ItemBuilder.asHead(HyrameHead.LIME_GREEN).build()), defaultValue, minimum, maximum, new Integer[] {50, 300}));
        return this;
    }

    public HostBorderCategory addFinalSizeOption(String optionName, int defaultValue, int minimum, int maximum) {
        this.addOption(18, this.finalSizeOption = new SizeOption(UsefulDisplay.optionDisplay(optionName, "border-final-size", ItemBuilder.asHead(HyrameHead.RED).build()), defaultValue, minimum, maximum, new Integer[] {10, 50}));
        return this;
    }

    public HostBorderCategory addSpeedOption(String optionName, double defaultValue, double minimum, double maximum) {
        this.addOption(27, this.speedOption = new SpeedOption(UsefulDisplay.optionDisplay(optionName, "border-speed", Material.FEATHER), defaultValue, minimum, maximum));
        return this;
    }

    public HostBorderCategory addTimeOption(String optionName, long defaultValue, long minimum, long maximum) {
        this.addOption(36, this.timeOption = new CustomTimeOption(UsefulDisplay.optionDisplay(optionName, "border-time", Material.WATCH), defaultValue, minimum, maximum));
        return this;
    }

    private class GUI extends HostGUI {

        public GUI(Player owner, HostCategory category) {
            super(owner, ChatColor.stripColor(category.getDisplayName().getValue(owner)), category);

            if (initialSizeOption != null) {
                initialSizeOption.handle(this.owner, this);
            } else if (finalSizeOption != null) {
                finalSizeOption.handle(this.owner, this);
            } else if (speedOption != null) {
                speedOption.handle(this.owner, this);
            } else if (timeOption != null) {
                timeOption.handle(this.owner, this);
            }
        }

        public void refresh() {
            this.addOptions();
        }

    }

    private static class GUIHandler<T> {

        private final Player player;
        private final GUI gui;
        private final Supplier<ItemStack> icon;

        private final Function<T, String> valueFormatter;
        private final Supplier<T> valueProvider;
        private final Consumer<T> valueModifier;

        private final BiConsumer<T, Boolean> onValueChange;

        private final T defaultValue;

        private final T maximum;
        private final T minimum;

        private final T[] modifiers;

        public GUIHandler(Player player, GUI gui,
                          Supplier<ItemStack> icon, Function<T, String> valueFormatter, Supplier<T> valueProvider, Consumer<T> valueModifier,
                          BiConsumer<T, Boolean> onValueChange,
                          T defaultValue, T maximum, T minimum, T[] modifiers) {

            this.player = player;
            this.gui = gui;
            this.icon = icon;
            this.valueFormatter = valueFormatter;
            this.valueProvider = valueProvider;
            this.valueModifier = valueModifier;
            this.onValueChange = onValueChange;
            this.defaultValue = defaultValue;
            this.maximum = maximum;
            this.minimum = minimum;
            this.modifiers = modifiers;

            this.gui.setItem(31, ItemBuilder.asHead(HyrameHead.GARBAGE_CAN)
                            .withName(HyrameMessage.HOST_RESET_NAME.asString(player))
                            .withLore(HyrameMessage.HOST_RESET_LORE.asList(player))
                            .build(),
                            event -> {
                                this.valueModifier.accept(this.defaultValue);

                                this.addItems();

                                player.playSound(player.getLocation(), Sound.FIZZ, 0.5F, 1.0F);
                            });

            this.addItems();
            this.addIcon();
        }

        private void addIcon() {
            this.gui.setItem(22, this.icon.get());
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
            final T modifier = this.modifiers[index];
            final ItemStack itemStack = ItemBuilder.asHead(head)
                    .withName(color + (plus ? "+" : "-") + this.valueFormatter.apply(modifier))
                    .withLore(ListReplacer.replace(HyrameMessage.HOST_MULTIPLE_MODIFIERS_LORE.asList(this.player), "%value%", this.valueFormatter.apply(this.valueProvider.get()))
                            .replace("%maximum%", this.valueFormatter.apply(this.maximum))
                            .replace("%minimum%", this.valueFormatter.apply(this.minimum))
                            .list())
                    .build();

            this.gui.setItem(slot, itemStack, event -> {
                this.onValueChange.accept(modifier, plus);

                this.player.playSound(this.player.getLocation(), Sound.CLICK, 0.5F, 2.0F);

                this.addItems();
                this.addIcon();

                this.gui.refresh();
            });
        }
    }

    private static class SizeOption extends PreciseIntegerOption {

        private final Integer[] modifiers;

        public SizeOption(HostDisplay display, int defaultValue, int minimum, int maximum, Integer[] modifiers) {
            super(display, defaultValue, minimum, maximum, new int[] {1, 1});
            this.modifiers = modifiers;
        }

        @Override
        public void onClick(Player player, InventoryClickEvent e) {
            final GUI gui = (GUI) this.categoryGUIProvider.apply(player);

            this.handle(player, gui);
        }

        public void handle(Player player, GUI gui) {
            new GUIHandler<>(player, gui, () -> new ItemBuilder(this.createItem(player)).removeLoreLines(2).build(), String::valueOf, () -> this.value, this::setValue, (object, plus) -> this.setValue(plus ? this.value + object : this.value - object), this.defaultValue, this.maximum, this.minimum, this.modifiers);

            gui.open();
        }

    }

    private static class SpeedOption extends DoubleOption {

        private final Double[] modifiers = new Double[] {0.5D, 2.0D};

        public SpeedOption(HostDisplay display, double defaultValue, double minimum, double maximum) {
            super(display, defaultValue, minimum, maximum);
            this.valueFormatter = player -> HyrameMessage.HOST_OPTION_BORDER_SPEED_FORMATTER.asString(player).replace("%speed%", String.valueOf(this.value));
        }

        @Override
        public void onClick(Player player, InventoryClickEvent e) {
            final GUI gui = (GUI) this.categoryGUIProvider.apply(player);

            this.handle(player, gui);
        }

        public void handle(Player player, GUI gui) {
            new GUIHandler<>(player, gui, () -> new ItemBuilder(this.createItem(player)).removeLoreLines(2).build(), value -> HyrameMessage.HOST_OPTION_BORDER_SPEED_VALUE_FORMATTER.asString(player).replace("%speed%", String.valueOf(value)), () -> this.value, this::setValue, (object, plus) -> this.setValue(plus ? this.value + object : this.value - object), this.defaultValue, this.maximum, this.minimum, this.modifiers);

            gui.open();
        }

    }

    private static class CustomTimeOption extends TimeOption {

        private final Long[] modifiers = new Long[] {60L, 5 * 60L};

        public CustomTimeOption(HostDisplay display, long defaultValue, long minimum, long maximum) {
            super(display, defaultValue, minimum, maximum, new long[] {1, 1});
        }

        @Override
        public void onClick(Player player, InventoryClickEvent event) {
            final GUI gui = (GUI) this.categoryGUIProvider.apply(player);

            this.handle(player, gui);
        }

        public void handle(Player player, GUI gui) {
            new GUIHandler<>(player, gui, () -> new ItemBuilder(this.createItem(player)).removeLoreLines(2).build(), TimeUtil::formatTime, () -> this.value, this::setValue, (object, plus) -> this.setValue(plus ? this.value + object : this.value - object), this.defaultValue, this.maximum, this.minimum, this.modifiers);

            gui.open();
        }

    }

}
