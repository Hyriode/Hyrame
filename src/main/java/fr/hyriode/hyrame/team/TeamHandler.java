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

public class TeamHandler implements Listener {
    @EventHandler
    public void onEntityDommageByEntity(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            if(TeamManager.getTeamByPlayer((Player) event.getEntity()) != null && TeamManager.getTeamByPlayer((Player) event.getDamager()) != null) {
                if(TeamManager.getTeamByPlayer((Player) event.getEntity()).equals(TeamManager.getTeamByPlayer((Player) event.getDamager()))) {
                    if(!TeamManager.getTeamByPlayer((Player) event.getEntity()).getFriendlyFire()) {
                        event.setCancelled(true);
                        event.setDamage(0.00F);
                    }
                }
            }

        }
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
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
