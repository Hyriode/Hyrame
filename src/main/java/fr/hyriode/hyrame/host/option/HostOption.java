package fr.hyriode.hyrame.host.option;

import fr.hyriode.api.host.HostData;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.host.HostDisplay;
import fr.hyriode.hyrame.host.IHostController;
import fr.hyriode.hyrame.host.gui.HostGUI;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyrameMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by AstFaster
 * on 31/07/2022 at 11:04
 */
public abstract class HostOption<T> extends HostDisplay {

    /** The NBT key of the representative item of a {@linkplain HostOption option} */
    public static final String NBT_KEY = "HostOption";

    /** The gui provider of the category */
    protected Function<Player, HostGUI> categoryGUIProvider;

    /** The formatter of the option's value */
    protected Function<Player, String> valueFormatter;
    /** A consumer called when the value changed */
    protected Consumer<T> onChanged;
    /** Is the option savable in a {@link fr.hyriode.api.host.IHostConfig} */
    protected boolean savable = true;
    /** Is the option accepting secondary host to change the value */
    protected boolean acceptingSecondaryHost = true;
    /** Is the option resettable */
    protected boolean resettable = true;

    /** The default value of the option */
    protected T defaultValue;
    /** The current value of the option */
    protected T value;

    /**
     * The main constructor of a {@link HostOption} object
     *
     * @param display The {@linkplain HostDisplay display} of the option
     * @param defaultValue The default value of the option
     */
    public HostOption(HostDisplay display, T defaultValue) {
        super(display.getName(), display.getDisplayName(), display.getDescription(), display.getIcon());

        if (this.getHostController().findOption(this.name) != null) {
            throw new IllegalArgumentException("Cannot create an option with the name '" + this.name + "' (reason: name already exists)!");
        }

        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    /**
     * This method is triggered each time the option is clicked
     *
     * @param player The {@linkplain Player player} that clicked
     * @param event The event triggered when the player clicked
     */
    public abstract void onClick(Player player, InventoryClickEvent event);

    @Override
    public ItemStack createItem(Player player) {
        return this.defaultItemCreation(player);
    }

    /**
     * Default item creation
     *
     * @param player The player used to create the item
     * @return The created {@link ItemStack}
     */
    protected ItemStack defaultItemCreation(Player player) {
        final ItemBuilder builder = new ItemBuilder(super.createItem(player));
        List<String> lore = builder.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        lore.add("");

        if (this.valueFormatter != null) {
            lore.addAll(Arrays.asList(this.valueFormatter.apply(player).split("\n")));
            lore.add("");
        }

        lore.add(HyrameMessage.CLICK_TO_EDIT.asString(player));

        return builder.withLore(lore)
                .withAllItemFlags()
                .nbt().setBoolean(NBT_KEY, true).build();
    }

    /**
     * Get the value of the option
     *
     * @return A value
     */
    public T getValue() {
        return this.value;
    }

    /**
     * Get the default value of the option
     *
     * @return A value
     */
    public T getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Cast the value of the option to a given class
     *
     * @param clazz The class used to cast the option's value
     * @return The cast value
     * @param <R> The type of the result
     */
    public <R> R castValue(Class<R> clazz) {
        return clazz.cast(this.value);
    }

    /**
     * Set the value of the option
     *
     * @param value The new value of the option
     */
    public void setValue(T value) {
        this.value = value;

        this.triggerChanged();
    }

    /**
     * Hard set the value of the option by giving an object.<br>
     * This method is unsafe, it might throw a {@link ClassCastException} if the given object is not of the same type of the {@linkplain HostOption option}.
     *
     * @param o The new value of the option
     */
    @SuppressWarnings("unchecked")
    public void setHardValue(Object o) {
        this.setValue((T) o);
    }

    /**
     * Triggered the {@link HostOption#onChanged} consumer
     */
    protected void triggerChanged() {
        if (this.onChanged == null) {
            return;
        }

        this.onChanged.accept(this.value);
    }

    /**
     * Set the consumer triggered when the value changes
     *
     * @param onChanged A consumer of a value
     * @return The current {@linkplain HostOption option} object
     */
    public HostOption<T> onChanged(Consumer<T> onChanged) {
        this.onChanged = onChanged;
        return this;
    }

    /**
     * Check if the option is savable
     *
     * @return <code>true</code> if yes
     */
    public boolean isSavable() {
        return this.savable;
    }

    /**
     * Set the option sable
     *
     * @return The current {@linkplain HostOption option} object
     */
    public HostOption<T> savable() {
        this.savable = true;
        return this;
    }

    /**
     * Set the option not savable
     *
     * @return The current {@linkplain HostOption option} object
     */
    public HostOption<T> notSavable() {
        this.savable = false;
        return this;
    }

    /**
     * Check if the option is accepting secondary host
     *
     * @return <code>true</code> if yes
     */
    public boolean isAcceptingSecondaryHost() {
        return this.acceptingSecondaryHost;
    }

    /**
     * Set if the option is accepting secondary host
     *
     * @return The current {@linkplain HostOption option} object
     */
    public HostOption<T> acceptingSecondaryHost() {
        this.acceptingSecondaryHost = true;
        return this;
    }

    /**
     * Set if the option is not accepting secondary host
     *
     * @return The current {@linkplain HostOption option} object
     */
    public HostOption<T> notAcceptingSecondaryHost() {
        this.acceptingSecondaryHost = false;
        return this;
    }

    /**
     * Check if the value is resettable
     *
     * @return <code>true</code> if yes
     */
    public boolean isResettable() {
        return this.resettable;
    }

    /**
     * Set the option resettable
     *
     * @return The current {@linkplain HostOption option} object
     */
    public HostOption<T> resettable() {
        this.resettable = true;
        return this;
    }

    /**
     * Set the option not resettable
     *
     * @return The current {@linkplain HostOption option} object
     */
    public HostOption<T> notResettable() {
        this.resettable = false;
        return this;
    }

    /**
     * Reset the option
     */
    public void reset() {
        this.setValue(this.defaultValue);
    }

    /**
     * Get formatter of the option's value
     *
     * @return A formatter
     */
    public Function<Player, String> getValueFormatter() {
        return this.valueFormatter;
    }

    /**
     * Set the gui provider of the option's category
     *
     * @param categoryGUIProvider A gui provider
     */
    public void setCategoryGUIProvider(Function<Player, HostGUI> categoryGUIProvider) {
        this.categoryGUIProvider = categoryGUIProvider;
    }

    /**
     * Get the {@link IHyrame} instance
     *
     * @return The {@link IHyrame} instance
     */
    protected IHyrame getHyrame() {
        return HyrameLoader.getHyrame();
    }

    /**
     * Get the {@link IHostController} instance
     *
     * @return The {@link IHostController} instance
     */
    protected IHostController getHostController() {
        return this.getHyrame().getHostController();
    }

    /**
     * Get the {@link HyriGame} instance
     *
     * @return The {@link HyriGame} instance
     */
    protected HyriGame<?> getGame() {
        return this.getHyrame().getGameManager().getCurrentGame();
    }

    /**
     * Get the data of the host
     *
     * @return A {@link HostData} object
     */
    protected HostData getHostData() {
        return this.getHostController().getHostData();
    }

}
