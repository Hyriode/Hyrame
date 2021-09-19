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

    private final ChatColor color;
    private final byte data;

    HyriGameTeamColor(ChatColor color, byte data) {
        this.color = color;
        this.data = data;
    }

    public ChatColor getColor() {
        return this.color;
    }

    public byte getData() {
        return this.data;
    }

}
