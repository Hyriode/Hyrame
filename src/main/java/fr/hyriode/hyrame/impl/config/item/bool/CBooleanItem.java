package fr.hyriode.hyrame.impl.config.item.bool;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.config.ConfigContext;
import fr.hyriode.hyrame.config.ConfigProcess;
import fr.hyriode.hyrame.config.handler.CBooleanHandler;
import fr.hyriode.hyrame.config.handler.ConfigOptionHandler;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.item.HyriItem;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by AstFaster
 * on 03/06/2022 at 19:44
 */
public abstract class CBooleanItem extends HyriItem<HyramePlugin> {

    private final boolean value;

    public CBooleanItem(HyramePlugin plugin, String name, String displayName, byte data, boolean value) {
        super(plugin, name, () -> HyriLanguageMessage.from(displayName), Material.INK_SACK, data);
        this.value = value;
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

        if (handler instanceof CBooleanHandler) {
            final CBooleanHandler booleanHandler = (CBooleanHandler) handler;

            booleanHandler.validate(this.value);
        }
    }


}
