package fr.hyriode.hyrame.team;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class TeamSelector implements Listener {

    ItemStack teamSelector = null;
    ArrayList<Team> teams = null;

    public ItemStack createTeamSelector(Material material, String name, ArrayList<String> lore, ArrayList<Team> teams) {
        ItemStack teamSelector = new ItemStack(material);
        teamSelector.getItemMeta().setDisplayName(name);
        teamSelector.getItemMeta().setLore(lore);

        this.teamSelector = teamSelector;
        this.teams = teams;
        return teamSelector;
    }

    @EventHandler
    public void OnPlayerClick(PlayerInteractEvent event) {
        if(event.getPlayer().getInventory().getItemInHand().equals(teamSelector)) {
            Inventory inventory;
            if(teams.size() > 9) {
                inventory = Bukkit.createInventory(event.getPlayer(), 18);
            }else {
                inventory = Bukkit.createInventory(event.getPlayer(), 9, "Selection des teams");
            }
            for(Team team : teams) {
                inventory.addItem(team.getTeamColor().getColoredWool(1, ChatColor.GOLD + team.toString()));
            }
        }
    }

    @EventHandler
    public void OnPlayerInventoryClick(InventoryClickEvent event) {
        if(event.getClickedInventory().getName().equalsIgnoreCase("Selection des teams")) {
            for(Team team : teams) {
                if(team.contains(event.getWhoClicked())) {
                    team.removeMember((Player) event.getWhoClicked());
                }
                if(team.getTeamColor().getColoredWool(1, ChatColor.GOLD + team.toString()).equals(event.getCurrentItem())) {
                    team.addMember((Player) event.getWhoClicked());
                    event.getWhoClicked().sendMessage("Vous avez été intégré à la team " + team);
                }
            }
        }
    }
}
