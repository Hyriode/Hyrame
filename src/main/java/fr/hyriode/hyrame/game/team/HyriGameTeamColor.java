package fr.hyriode.hyrame.game.team;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 10/09/2021 at 10:54
 */
public enum HyriGameTeamColor {

    RED(ChatColor.RED, (byte) 14, DyeColor.RED),
    YELLOW(ChatColor.YELLOW, (byte) 4, DyeColor.YELLOW),
    DARK_GREEN(ChatColor.DARK_GREEN, (byte) 13, DyeColor.GREEN),
    GREEN(ChatColor.GREEN, (byte) 5, DyeColor.LIME),
    CYAN(ChatColor.AQUA, (byte) 9, DyeColor.CYAN),
    BLUE(ChatColor.BLUE, (byte) 11, DyeColor.BLUE),
    PINK(ChatColor.LIGHT_PURPLE, (byte) 6, DyeColor.PINK),
    PURPLE(ChatColor.DARK_PURPLE, (byte) 10, DyeColor.PURPLE),
    WHITE(ChatColor.WHITE, (byte) 0, DyeColor.WHITE),
    GRAY(ChatColor.DARK_GRAY, (byte) 7, DyeColor.GRAY),
    BLACK(ChatColor.BLACK, (byte) 15, DyeColor.BLACK);

    /** The Spigot color */
    private final ChatColor chatColor;
    /** The color data, for wools, colored blocks etc */
    private final byte data;
    /** The color used for dyes */
    private final DyeColor dyeColor;

    /**
     * Constructor of {@link HyriGameTeamColor}
     *
     * @param chatColor Spigot {@link ChatColor}
     * @param data Color data
     * @param dyeColor The color used for dyes
     */
    HyriGameTeamColor(ChatColor chatColor, byte data, DyeColor dyeColor) {
        this.chatColor = chatColor;
        this.data = data;
        this.dyeColor = dyeColor;
    }

    /**
     * Get the Spigot color
     *
     * @return {@link ChatColor}
     */
    public ChatColor getChatColor() {
        return this.chatColor;
    }

    /**
     * Get the color data
     *
     * @return - Color data
     */
    public byte getData() {
        return this.data;
    }

    /**
     * Get the {@link DyeColor} object
     *
     * @return {@link DyeColor}
     */
    public DyeColor getDyeColor() {
        return this.dyeColor;
    }

}
