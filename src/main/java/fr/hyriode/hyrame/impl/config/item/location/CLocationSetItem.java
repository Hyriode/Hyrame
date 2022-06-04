package fr.hyriode.hyrame.impl.config.item.location;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.config.ConfigContext;
import fr.hyriode.hyrame.config.ConfigProcess;
import fr.hyriode.hyrame.config.handler.CLocationHandler;
import fr.hyriode.hyrame.config.handler.ConfigOptionHandler;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.utils.LocationWrapper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by AstFaster
 * on 02/06/2022 at 18:41
 */
public class CLocationSetItem extends HyriItem<HyramePlugin> {

    public CLocationSetItem(HyramePlugin plugin) {
        super(plugin, CLocationHandler.SET_ITEM, displayName("item.config.location.set"), Material.MAGMA_CREAM);
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ConfigProcess<?> process = hyrame.getConfigManager().getProcess(player.getUniqueId());

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

            locationHandler.provideLocation(LocationWrapper.from(player.getLocation()));
        }
    }

}
