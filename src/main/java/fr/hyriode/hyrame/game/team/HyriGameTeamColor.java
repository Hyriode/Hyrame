package fr.hyriode.hyrame.game.team;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.language.HyrameMessage;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

import java.util.function.Supplier;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 10/09/2021 at 10:54
 */
public enum HyriGameTeamColor {

    RED(ChatColor.RED, DyeColor.RED, HyrameMessage.COLOR_RED::asLang),
    ORANGE(ChatColor.GOLD, DyeColor.ORANGE, HyrameMessage.COLOR_ORANGE::asLang),
    YELLOW(ChatColor.YELLOW, DyeColor.YELLOW, HyrameMessage.COLOR_YELLOW::asLang),
    DARK_GREEN(ChatColor.DARK_GREEN, DyeColor.GREEN, HyrameMessage.COLOR_DARK_GREEN::asLang),
    GREEN(ChatColor.GREEN, DyeColor.LIME, HyrameMessage.COLOR_GREEN::asLang),
    CYAN(ChatColor.AQUA, DyeColor.CYAN, HyrameMessage.COLOR_CYAN::asLang),
    BLUE(ChatColor.BLUE, DyeColor.BLUE, HyrameMessage.COLOR_BLUE::asLang),
    PINK(ChatColor.LIGHT_PURPLE, DyeColor.PINK, HyrameMessage.COLOR_PINK::asLang),
    PURPLE(ChatColor.DARK_PURPLE, DyeColor.PURPLE, HyrameMessage.COLOR_PURPLE::asLang),
    WHITE(ChatColor.WHITE, DyeColor.WHITE, HyrameMessage.COLOR_WHITE::asLang),
    GRAY(ChatColor.GRAY, DyeColor.GRAY, HyrameMessage.COLOR_GRAY::asLang),
    BLACK(ChatColor.DARK_GRAY, DyeColor.BLACK, HyrameMessage.COLOR_BLACK::asLang);

    /** The Spigot color */
    private final ChatColor chatColor;
    /** The color used for dyes */
    private final DyeColor dyeColor;
    /** The display of the color */
    private final Supplier<HyriLanguageMessage> display;

    /**
     * Constructor of {@link HyriGameTeamColor}
     *
     * @param chatColor Spigot {@link ChatColor}
     * @param dyeColor The color used for dyes
     * @param display The display of the color
     */
    HyriGameTeamColor(ChatColor chatColor, DyeColor dyeColor, Supplier<HyriLanguageMessage> display) {
        this.chatColor = chatColor;
        this.dyeColor = dyeColor;
        this.display = display;
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

    /**
     * Get the display of the color
     *
     * @return A {@link HyriLanguageMessage} object
     */
    public HyriLanguageMessage getDisplay() {
        return this.display.get();
    }

}
