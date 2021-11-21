package fr.hyriode.hyrame.game.team;

import org.bukkit.ChatColor;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 10/09/2021 at 10:54
 */
public enum HyriGameTeamColor {

    RED(ChatColor.RED, (byte) 14),
    YELLOW(ChatColor.YELLOW, (byte) 4),
    DARK_GREEN(ChatColor.DARK_GREEN, (byte) 13),
    GREEN(ChatColor.GREEN, (byte) 5),
    CYAN(ChatColor.AQUA, (byte) 9),
    BLUE(ChatColor.BLUE, (byte) 11),
    PINK(ChatColor.LIGHT_PURPLE, (byte) 6),
    PURPLE(ChatColor.DARK_PURPLE, (byte) 10),
    WHITE(ChatColor.WHITE, (byte) 0),
    GRAY(ChatColor.DARK_GRAY, (byte) 7),
    BLACK(ChatColor.BLACK, (byte) 15);

    /** The Spigot color */
    private final ChatColor color;

    /** The color data, for wools, colored blocks etc */
    private final byte data;

    /**
     * Constructor of {@link HyriGameTeamColor}
     *
     * @param color - Spigot color
     * @param data - Color data
     */
    HyriGameTeamColor(ChatColor color, byte data) {
        this.color = color;
        this.data = data;
    }

    /**
     * Get the Spigot color
     *
     * @return - {@link ChatColor}
     */
    public ChatColor getColor() {
        return this.color;
    }

    /**
     * Get the color data
     *
     * @return - Color data
     */
    public byte getData() {
        return this.data;
    }

}
