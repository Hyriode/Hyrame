package fr.hyriode.hyrame.title;

import fr.hyriode.hyrame.reflection.Reflection;
import fr.hyriode.hyrame.utils.PacketUtil;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by Keinz
 * on 12/11/2021 at 15:25
 */
public class Title {

    /**
     * Send a title to a given player
     *
     * @param player Concerned player
     * @param title Title to send
     * @param subTitle Subtitle to send
     * @param fadeIn Time to appear on player screen
     * @param stay Time to stay on player screen
     * @param fadeOut Time to disappear from player screen
     */
    public static void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        PacketUtil.sendPacket(player, new PacketPlayOutTitle(fadeIn, stay, fadeOut));

        if (title != null) {
            PacketUtil.sendPacket(player, new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatComponentText(title)));
        }
        if (subTitle != null) {
            PacketUtil.sendPacket(player, new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, new ChatComponentText(subTitle)));
        }
    }

    /**
     * Send a title to a given player
     *
     * @param player Concerned player
     * @param title The title object to send
     */
    public static void sendTitle(Player player, Title title) {
        sendTitle(player, title.getTitle(), title.getSubTitle(), title.getFadeIn(), title.getStay(), title.getFadeout());
    }

    /**
     * Send a title to all the online players
     *
     * @param title The title object to send
     */
    public static void sendTitle(Title title) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendTitle(player, title);
        }
    }

    /**
     * Set player tab list header and footer
     *
     * @param player Concerned player
     * @param header Tab list header
     * @param footer Tab list footer
     */
    public static void setTabTitle(Player player, String header, String footer) {
        if (header == null) header = "";
        if (footer == null) footer = "";

        final PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

        Reflection.setField("a", packet, new ChatComponentText(header));
        Reflection.setField("b", packet, new ChatComponentText(footer));

        PacketUtil.sendPacket(player, packet);
    }

    /** The first line of the title that will appear on player screen */
    private String title = "";
    /** The second line of the title that will appear on player screen */
    private String subTitle = "";
    /** The time that will take the title to appear on player screen */
    private int fadeIn = 10;
    /** The time that will take the title to stay on player screen */
    private int stay = 4 * 20;
    /** The time that will take the title to disappear from player screen */
    private int fadeout = 10;

    /**
     * Empty {@link Title} constructor
     */
    public Title() {}

    /**
     * Full constructor of {@link Title}
     *
     * @param title The first line of the title to send
     * @param subTitle The second line of the title to send
     * @param fadeIn The time that will take the title to appear on player screen
     * @param stay The time that will take the title to stay on player screen
     * @param fadeout The time that will take the title to disappear from player screen
     */
    public Title(String title, String subTitle, int fadeIn, int stay, int fadeout) {
        this.title = title;
        this.subTitle = subTitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeout = fadeout;
    }

    /**
     * Get the title
     *
     * @return A title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Set the title
     *
     * @param title A new title
     * @return This {@link Title} object
     */
    public Title setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Get the subtitle
     *
     * @return A subtitle
     */
    public String getSubTitle() {
        return this.subTitle;
    }

    /**
     * Set the subtitle
     *
     * @param subTitle A new subtitle
     * @return This {@link Title} object
     */
    public Title setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    /**
     * Get the fade in time
     *
     * @return A time (in ticks)
     */
    public int getFadeIn() {
        return this.fadeIn;
    }

    /**
     * Set the fade in time
     *
     * @param fadeIn A new fade in time
     * @return This {@link Title} object
     */
    public Title setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
        return this;
    }

    /**
     * Get the stay time
     *
     * @return A time (in ticks)
     */
    public int getStay() {
        return this.stay;
    }

    /**
     * Set the stay time
     *
     * @param stay A new stay time
     * @return This {@link Title} object
     */
    public Title setStay(int stay) {
        this.stay = stay;
        return this;
    }

    /**
     * Get the fade out time
     *
     * @return A time (in ticks)
     */
    public int getFadeout() {
        return this.fadeout;
    }

    /**
     * Set the fade out time
     *
     * @param fadeout A new fade out time
     * @return This {@link Title} object
     */
    public Title setFadeout(int fadeout) {
        this.fadeout = fadeout;
        return this;
    }

}