package fr.hyriode.hyrame.impl.listener.model.global;

import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.listener.HyriListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 01/01/2022 at 13:39
 */
public class WorldListener extends HyriListener<HyramePlugin> {

    public WorldListener(HyramePlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

}
