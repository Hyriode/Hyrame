package fr.hyriode.hyrame.game.team;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 10/09/2021 at 10:54
 */
public enum HyriGameTeamColor {

    RED(ChatColor.RED, DyeColor.RED),
    ORANGE(ChatColor.GOLD, DyeColor.ORANGE),
    YELLOW(ChatColor.YELLOW, DyeColor.YELLOW),
    DARK_GREEN(ChatColor.DARK_GREEN, DyeColor.GREEN),
    GREEN(ChatColor.GREEN, DyeColor.LIME),
    CYAN(ChatColor.AQUA, DyeColor.CYAN),
    BLUE(ChatColor.BLUE, DyeColor.BLUE),
    PINK(ChatColor.LIGHT_PURPLE, DyeColor.PINK),
    PURPLE(ChatColor.DARK_PURPLE, DyeColor.PURPLE),
    WHITE(ChatColor.WHITE, DyeColor.WHITE),
    GRAY(ChatColor.GRAY, DyeColor.GRAY),
    BLACK(ChatColor.DARK_GRAY, DyeColor.BLACK);

    /** The Spigot color */
    private final ChatColor chatColor;
    /** The color used for dyes */
    private final DyeColor dyeColor;

    /**
     * Constructor of {@link HyriGameTeamColor}
     *
     * @param chatColor Spigot {@link ChatColor}
     * @param dyeColor The color used for dyes
     */
    HyriGameTeamColor(ChatColor chatColor, DyeColor dyeColor) {
        this.chatColor = chatColor;
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
     * Get the {@link DyeColor} object
     *
     * @return {@link DyeColor}
     */
    public DyeColor getDyeColor() {
        return this.dyeColor;
    }

}
