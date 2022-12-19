package fr.hyriode.hyrame.impl.config.item.location;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.config.ConfigContext;
import fr.hyriode.hyrame.config.ConfigProcess;
import fr.hyriode.hyrame.config.handler.CLocationHandler;
import fr.hyriode.hyrame.config.handler.ConfigOptionHandler;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.Symbols;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by AstFaster
 * on 03/06/2022 at 10:38
 */
public class CLocationEyesItem extends HyriItem<HyramePlugin> {

    public CLocationEyesItem(HyramePlugin plugin) {
        super(plugin, CLocationHandler.EYES_ITEM, () -> HyriLanguageMessage.get("item.config.location.eyes"), null, Material.EYE_OF_ENDER);
    }

    @Override
    public ItemStack onPreGive(IHyrame hyrame, Player player, int slot, ItemStack itemStack) {
        final ConfigProcess<?> process = hyrame.getConfigManager().getProcess(player.getUniqueId());

        if (process == null) {
            return itemStack;
        }

        final ConfigContext context = process.current();

        if (context == null) {
            return itemStack;
        }

        final ConfigOptionHandler<?> handler = context.getHandler();

        if (handler instanceof CLocationHandler) {
            final CLocationHandler locationHandler = (CLocationHandler) handler;

            return new ItemBuilder(itemStack)
                    .withName(itemStack.getItemMeta().getDisplayName().replace("%value%", locationHandler.isEyes() ? ChatColor.GREEN + Symbols.TICK_BOLD : ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD))
                    .build();
        }
        return itemStack;
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        final ConfigProcess<?> process = hyrame.getConfigManager().getProcess(event.getPlayer().getUniqueId());

        if (process == null) {
            return;
        }

        final ConfigContext context = process.current();

        if (context == null) {
            return;
        }

        final ConfigOptionHandler<?> handler = context.getHandler();

        if (handler instanceof CLocationHandler) {
            final CLocationHandler locationHandler = (CLocationHandler) handler;

            locationHandler.setEyes(!locationHandler.isEyes());
        }
    }

}
