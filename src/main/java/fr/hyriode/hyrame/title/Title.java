package fr.hyriode.hyrame.title;

import fr.hyriode.hyrame.reflection.Reflection;
import fr.hyriode.hyrame.utils.PacketUtil;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
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

}