package fr.hyriode.hyrame.impl.config.item.bool;

import fr.hyriode.hyrame.config.handler.CBooleanHandler;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.utils.Symbols;
import org.bukkit.ChatColor;

/**
 * Created by AstFaster
 * on 03/06/2022 at 11:05
 */
public class CBooleanFalseItem extends CBooleanItem {

    public CBooleanFalseItem(HyramePlugin plugin) {
        super(plugin, CBooleanHandler.NO_ITEM, ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD, (byte) 1, false);
    }

}
