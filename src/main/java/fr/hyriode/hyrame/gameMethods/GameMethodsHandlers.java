package fr.hyriode.hyrame.gameMethods;

import fr.hyriode.hyrame.Hyrame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class GameMethodsHandlers implements Listener {

    @EventHandler
    public void onPlayerSpeak(AsyncPlayerChatEvent event) {
        if(GamePlayerManager.gamePlayerByPlayer(event.getPlayer()) != null) {
            GamePlayer gamePlayer = GamePlayerManager.gamePlayerByPlayer(event.getPlayer());
            if(!gamePlayer.isAlive()) {
                if(!gamePlayer.getCanSpeakWhenDead()) {
                    event.setCancelled(true);
                    for(GamePlayer deadPlayer : gamePlayer.getGame().deadPlayers) {
                        deadPlayer.getPlayer().sendMessage( event.getPlayer().getName() + ": " + event.getMessage());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntitytakeDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if(GamePlayerManager.gamePlayerByPlayer(player) != null) {
                if(player.getHealth() - event.getFinalDamage() <= 0) {
                    GamePlayer gamePlayer = GamePlayerManager.gamePlayerByPlayer(player);
                    gamePlayer.kill();
                    event.setCancelled(true);
                }
            }
        }
    }
}
