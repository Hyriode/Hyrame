package fr.hyriode.hyrame.game;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 17/03/2022 at 19:09
 */
public class HyriGameInfo {

    /** The representative icon of the game */
    private final Material icon;
    /** The name of the game */
    private final String name;
    /** The display name of the game */
    private final String displayName;
    /** A list of all {@link HyriGameType} available for the game */
    private final List<HyriGameType> types;

    /**
     * Constructor of {@link HyriGameInfo}
     *
     * @param icon A {@link Material} used to represent the game
     * @param name The name of the game
     * @param displayName The display name of the game
     */
    public HyriGameInfo(Material icon, String name, String displayName) {
        this.icon = icon;
        this.name = name;
        this.displayName = displayName;
        this.types = new ArrayList<>();
    }

    /**
     * Get the representative icon of the game
     *
     * @return A {@link Material}
     */
    public Material getIcon() {
        return this.icon;
    }

    /**
     * Get the name of the game.<br>
     * Example: bedwars, rtf
     *
     * @return A game name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the display name of the game
     *
     * @return A display name
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Add a {@link HyriGameType} to the game info
     *
     * @param types Types to add
     */
    public void addTypes(HyriGameType... types) {
        this.types.addAll(Arrays.asList(types));
    }

    /**
     * Remove a {@link HyriGameType} from the game info
     *
     * @param type The type to remove
     */
    public void removeType(HyriGameType type) {
        this.types.remove(type);
    }

    /**
     * Get the current list of {@link HyriGameType}
     *
     * @return A {@link List}
     */
    public List<HyriGameType> getTypes() {
        return this.types;
    }

}
