package fr.hyriode.hyrame.host;

import fr.hyriode.api.host.HostData;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.host.gui.HostGUI;
import fr.hyriode.hyrame.host.option.HostOption;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyrameMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by AstFaster
 * on 31/07/2022 at 11:02
 */
public class HostCategory extends HostDisplay {

    /** The NBT key of the representative item of the {@linkplain HostCategory category} */
    public static final String NBT_KEY = "HostCategory";

    /** The map of {@linkplain HostOption option} linked to their slot */
    protected final Map<Integer, HostOption<?>> options;
    /** The map of {@linkplain HostCategory category} linked to their slot */
    protected final Map<Integer, HostCategory> subCategories;

    /** The provider of the parent {@linkplain HostGUI gui} */
    protected Function<Player, HostGUI> parentGUIProvider;
    /** The provider of the {@linkplain HostGUI gui} */
    protected Function<Player, HostGUI> guiProvider;

    /**
     * The main constructor of a {@linkplain HostCategory host category}
     *
     * @param display The display of the host category
     */
    public HostCategory(HostDisplay display) {
        super(display.getName(), display.getDisplayName(), display.getDescription(), display.getIcon());
        this.options = new HashMap<>();
        this.subCategories = new HashMap<>();
        this.guiProvider = player -> new HostGUI(player, ChatColor.stripColor(this.displayName.getValue(player)), this);
    }

    /**
     * Open the gui of the {@linkplain HostCategory category} to a given player
     *
     * @param player The player that will have the GUI open
     */
    public void openGUI(Player player) {
        if (this.guiProvider == null) {
            return;
        }

        this.guiProvider.apply(player).open();
    }

    @Override
    public ItemStack createItem(Player player) {
        final ItemBuilder builder = new ItemBuilder(super.createItem(player));
        List<String> lore = builder.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        lore.add("");
        lore.add(HyrameMessage.CLICK_TO_SEE.asString(player));

        return builder.withLore(lore).nbt().setBoolean(NBT_KEY, true).build();
    }

    /**
     * Add an option to a slot
     *
     * @param slot The slot where the option will be displayed
     * @param option The option to add
     */
    public void addOption(int slot, HostOption<?> option) {
        option.setCategoryGUIProvider(this.guiProvider);

        this.options.put(slot, option);
    }

    /**
     * Get the gui provider of the category
     *
     * @return A gui provider
     */
    public Function<Player, HostGUI> getGUIProvider() {
        return this.guiProvider;
    }

    /**
     * Get the gui provider of the parent category
     *
     * @return A gui provider
     */
    public Function<Player, HostGUI> getParentGUIProvider() {
        return this.parentGUIProvider;
    }

    /**
     * Set the parent gui provider
     *
     * @param parentGUIProvider A gui provider
     */
    public void setParentGUIProvider(Function<Player, HostGUI> parentGUIProvider) {
        this.parentGUIProvider = parentGUIProvider;
    }

    /**
     * Get all the options of the category
     *
     * @return A map of {@linkplain HostOption option} linked to their slot
     */
    public Map<Integer, HostOption<?>> getOptions() {
        return this.options;
    }

    /**
     * Add a sub-category to the category
     *
     * @param slot The slot where the {@linkplain HostCategory sub-category} will be displayed
     * @param category The {@linkplain HostCategory sub-category} to add
     */
    public void addSubCategory(int slot, HostCategory category) {
        category.setParentGUIProvider(this.guiProvider);

        this.subCategories.put(slot, category);
    }

    /**
     * Get all the sub-categories of the category
     *
     * @return A map of {@linkplain HostCategory category} linked to their slot
     */
    public Map<Integer, HostCategory> getSubCategories() {
        return this.subCategories;
    }

    /**
     * Get the host controller instance
     *
     * @return The {@linkplain IHostController host controller} instance
     */
    protected IHostController getHostController() {
        return HyrameLoader.getHyrame().getHostController();
    }

    /**
     * Get the current game instance
     *
     * @return The {@linkplain HyriGame game} instance
     */
    protected HyriGame<?> getGame() {
        return HyrameLoader.getHyrame().getGameManager().getCurrentGame();
    }

    /**
     * Get the data of the current host
     *
     * @return A {@link HostData} object
     */
    protected HostData getHostData() {
        return this.getHostController().getHostData();
    }

}
