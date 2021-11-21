package fr.hyriode.tools.tab;

import fr.hyriode.tools.PacketUtil;
import fr.hyriode.tools.reflection.Reflection;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class TabTitle {

    /**
     * Set player tab list title
     *
     * @param player - Player
     * @param header - Tab list header
     * @param footer - Tab list footer
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
