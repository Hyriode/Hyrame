package fr.hyriode.hyrame.impl.listener.global;

import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.listener.HyriListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 02/03/2022 at 18:42
 */
public class PlayerListener extends HyriListener<HyramePlugin> {

    public PlayerListener(HyramePlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

}