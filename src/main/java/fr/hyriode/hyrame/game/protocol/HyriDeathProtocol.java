package fr.hyriode.hyrame.game.protocol;

import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 24/02/2022 at 19:53
 */
public class HyriDeathScreen extends HyriGameProtocol implements Listener {

    private static final HyriLanguageMessage DEAD = new HyriLanguageMessage("")
            .addValue(HyriLanguage.EN, "dead")
            .addValue(HyriLanguage.FR, "mort");

    private static final HyriLanguageMessage RESPAWN = new HyriLanguageMessage("")
            .addValue(HyriLanguage.EN, "Respawn in")
            .addValue(HyriLanguage.FR, "Réapparition dans");

    private final JavaPlugin plugin;

    public HyriDeathScreen(JavaPlugin plugin) {
        super("death");
        this.plugin = plugin;
    }

    @Override
    void enable() {
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();

            if (player.getHealth() - event.getFinalDamage() <= 0) {
                player.setHealth(20.0F);
            }
        }
    }

}
