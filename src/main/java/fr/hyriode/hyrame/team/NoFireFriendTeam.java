package fr.hyriode.hyrame.team;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;

public class NoFireFriendTeam implements Listener {
    public static ArrayList<Team> noFireFriend = new ArrayList<Team>();

    @EventHandler
    public void OnEntityDommageByEntity(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            if(noFireFriend != null) {
                for(Team team : noFireFriend) {
                    for(Player player : team.getMembers()) {
                        Bukkit.broadcastMessage(player.getDisplayName());
                        for(Player player1 : team.getMembers()) {
                            Bukkit.broadcastMessage(player1.getDisplayName());
                            if(event.getDamager().equals(player) && event.getEntity().equals(player1) || event.getEntity().equals(player) && event.getDamager().equals(player1)) {
                                event.setDamage(0.0F);
                                event.setCancelled(true);
                            }
                        }
                    }
                    if(team.contains(event.getDamager()) && team.contains(event.getEntity())) {

                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
