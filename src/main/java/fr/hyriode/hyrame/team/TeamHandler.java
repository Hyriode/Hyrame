package fr.hyriode.hyrame.team;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

public class TeamHandler implements Listener {
    public static ArrayList<Team> noFireFriend = new ArrayList<Team>();
    @EventHandler
    public void onEntityDommageByEntity(EntityDamageByEntityEvent event) {
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

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        Bukkit.broadcastMessage("aaa");
        Bukkit.broadcastMessage(event.getPlayer().getInventory().getItemInHand().toString());
        Bukkit.broadcastMessage(String.valueOf(TeamSelector.teamSelector));
        if(event.getPlayer().getInventory().getItemInHand().equals(TeamSelector.teamSelector)) {
            Inventory inventory;
            if(TeamSelector.teams.size() > 9) {
                inventory = Bukkit.createInventory(event.getPlayer(), 18);
            }else {
                inventory = Bukkit.createInventory(event.getPlayer(), 9, "Selection des teams");
            }
            for(Team team : TeamSelector.teams) {
                inventory.addItem(team.getTeamColor().getColoredWool(1, ChatColor.AQUA + team.getTeamColor().toString().toLowerCase()));
            }
            event.getPlayer().openInventory(inventory);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        if(event.getClickedInventory().getName().equalsIgnoreCase("Selection des teams")) {
            for(Team team : TeamSelector.teams) {
                team.removeMember((Player) event.getWhoClicked());
                if(team.getTeamColor().getColoredWool(1, ChatColor.AQUA + team.getTeamColor().toString().toLowerCase()).equals(event.getCurrentItem())) {
                    if(team.addMember((Player) event.getWhoClicked())) {
                        event.getWhoClicked().sendMessage(ChatColor.YELLOW + "Vous avez été intégré à la team " + team.getTeamColor().getChatColor() + team.getTeamColor());
                    }else {
                       event.getWhoClicked().sendMessage(ChatColor.DARK_RED + "Vous n'avez pas pu être ajouter car la team est pleine ou vous êtes déjà dedans");
                    }
                break;
                }
            }
            event.setCancelled(true);
        }
    }
}
