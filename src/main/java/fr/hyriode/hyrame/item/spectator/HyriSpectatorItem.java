package fr.hyriode.hyrame.item.spectator;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collections;

public abstract class HyriSpectatorItem extends HyriItem<HyramePlugin> {

    protected final IHyriLanguageManager lang;

    public HyriSpectatorItem(HyramePlugin plugin, String name, String displayName, String description, Material material) {
        super(plugin, name,
                () -> plugin.getHyrame().getLanguageManager().getMessage(displayName),
                () -> Collections.singletonList(plugin.getHyrame().getLanguageManager().getMessage(description)), material);

        this.lang = plugin.getHyrame().getLanguageManager();
    }

    protected abstract void onClick(IHyrame hyrame, PlayerInteractEvent event);

    @Override
    public void onLeftClick(IHyrame hyrame, PlayerInteractEvent event) {
        this.onClick(hyrame, event);
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        this.onClick(hyrame, event);
    }
}
