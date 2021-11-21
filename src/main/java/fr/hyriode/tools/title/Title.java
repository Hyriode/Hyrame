package fr.hyriode.tools.title;

import fr.hyriode.tools.PacketUtil;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class Title {

    /**
     * Send a title to a given player
     *
     * @param player - Player
     * @param title - Title to send
     * @param subTitle - Sub title to send
     * @param fadeIn - Time to appear on player screen
     * @param stay - Time to stay on player screen
     * @param fadeOut - Time to disappear from player screen
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

}