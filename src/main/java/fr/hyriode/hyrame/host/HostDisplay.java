package fr.hyriode.hyrame.host;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyggdrasil.api.util.builder.BuildException;
import fr.hyriode.hyggdrasil.api.util.builder.BuilderEntry;
import fr.hyriode.hyggdrasil.api.util.builder.IBuilder;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by AstFaster
 * on 31/07/2022 at 11:06
 */
public class HostDisplay {

    /** The NBT key of the representative item of the {@linkplain HostDisplay display} */
    public static final String NBT_KEY = "HostDisplay";

    /** The name of the display */
    protected String name;
    /** The display name of the display */
    protected HyriLanguageMessage displayName;
    /** The description of the display */
    protected HyriLanguageMessage description;
    /** The representative {@linkplain ItemStack item} of the display */
    protected ItemStack icon;

    /**
     * The main constructor of a {@linkplain HostDisplay host display}
     *
     * @param name The name of the display
     * @param displayName The display name of the display
     * @param description The description of the display
     * @param icon The representative icon of the display
     */
    public HostDisplay(String name, HyriLanguageMessage displayName, HyriLanguageMessage description, ItemStack icon) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
    }

    /**
     * Create the representative item of the display for a player
     *
     * @param player The {@linkplain Player player}
     * @return The created {@linkplain ItemStack representative item}
     */
    public ItemStack createItem(Player player) {
        return new ItemBuilder(this.icon.clone())
                .withName(this.displayName.getValue(player))
                .withLore(this.description != null ? new ArrayList<>(Arrays.asList(this.description.getValue(player).split("\n"))) : new ArrayList<>())
                .nbt().setString(NBT_KEY, this.name).build();
    }

    /**
     * Get the name of the display
     *
     * @return A name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the name of the display
     *
     * @param name The new name of the display
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the display name of the display
     *
     * @return A {@link HyriLanguageMessage}
     */
    public HyriLanguageMessage getDisplayName() {
        return this.displayName;
    }

    /**
     * Set the display name of the display
     *
     * @param displayName The new display name
     */
    public void setDisplayName(HyriLanguageMessage displayName) {
        this.displayName = displayName;
    }

    /**
     * Get the description of the display
     *
     * @return A {@link HyriLanguageMessage}
     */
    public HyriLanguageMessage getDescription() {
        return this.description;
    }

    /**
     * Set the description of the display
     *
     * @param description The new description
     */
    public void setDescription(HyriLanguageMessage description) {
        this.description = description;
    }

    /**
     * Get the representative icon of the display
     *
     * @return An {@link ItemStack}
     */
    public ItemStack getIcon() {
        return this.icon;
    }

    /**
     * Set the representative icon of the display
     *
     * @param icon The new icon
     */
    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    /**
     * The builder class of a {@linkplain HostDisplay host display}
     */
    public static class Builder implements IBuilder<HostDisplay> {

        private final BuilderEntry<String> nameEntry = new BuilderEntry<String>("Name entry").required();
        private final BuilderEntry<HyriLanguageMessage> displayNameEntry = new BuilderEntry<HyriLanguageMessage>("Display name entry").required();
        private final BuilderEntry<HyriLanguageMessage> descriptionEntry = new BuilderEntry<>("Description entry", () -> null);
        private final BuilderEntry<ItemStack> iconEntry = new BuilderEntry<ItemStack>("Icon entry").required();

        public Builder withName(String name) {
            this.nameEntry.set(() -> name);
            return this;
        }

        public Builder withDisplayName(HyriLanguageMessage displayName) {
            this.displayNameEntry.set(() -> displayName);
            return this;
        }

        public Builder withDescription(HyriLanguageMessage description) {
            this.descriptionEntry.set(() -> description);
            return this;
        }

        public Builder withIcon(ItemStack icon) {
            this.iconEntry.set(() -> icon);
            return this;
        }

        public Builder withIcon(Material material) {
            this.iconEntry.set(() -> new ItemStack(material));
            return this;
        }

        @Override
        public HostDisplay build() throws BuildException {
            return new HostDisplay(this.nameEntry.get(), this.displayNameEntry.get(), this.descriptionEntry.get(), this.iconEntry.get());
        }

    }

}
