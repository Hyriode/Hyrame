package fr.hyriode.hyrame.item.event;

import fr.hyriode.api.event.HyriCancellableEvent;
import fr.hyriode.hyrame.item.HyriItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by AstFaster
 * on 29/08/2022 at 14:44
 */
public class HyriItemGiveEvent extends HyriCancellableEvent {

    private final UUID player;
    private final HyriItem<?> item;

    public HyriItemGiveEvent(UUID player, HyriItem<?> item) {
        this.player = player;
        this.item = item;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.player);
    }

    public HyriItem<?> getItem() {
        return this.item;
    }

}
