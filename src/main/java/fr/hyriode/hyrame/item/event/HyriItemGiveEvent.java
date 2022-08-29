package fr.hyriode.hyrame.item.event;

import fr.hyriode.api.event.HyriCancellableEvent;
import fr.hyriode.hyrame.item.HyriItem;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 29/08/2022 at 14:44
 */
public class HyriItemGiveEvent extends HyriCancellableEvent {

    private final Player player;
    private final HyriItem<?> item;

    public HyriItemGiveEvent(Player player, HyriItem<?> item) {
        this.player = player;
        this.item = item;
    }

    public Player getPlayer() {
        return this.player;
    }

    public HyriItem<?> getItem() {
        return this.item;
    }

}
