package fr.hyriode.hyrame.impl.config.item.bool;

import fr.hyriode.hyrame.config.handler.CBooleanHandler;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.utils.Symbols;
import org.bukkit.ChatColor;

/**
 * Created by AstFaster
 * on 03/06/2022 at 11:05
 */
public class CBooleanTrueItem extends CBooleanItem {

    public CBooleanTrueItem(HyramePlugin plugin) {
        super(plugin, CBooleanHandler.YES_ITEM, ChatColor.GREEN + Symbols.TICK_BOLD, (byte) 10, true);
    }

}
