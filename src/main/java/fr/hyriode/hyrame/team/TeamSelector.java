package fr.hyriode.hyrame.team;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class TeamSelector implements Listener {

    public static ItemStack teamSelector = null;
    public static ArrayList<Team> teams = new ArrayList<Team>();

    public static ItemStack createTeamSelector(Material material, String name, ArrayList<String> lore, ArrayList<Team> teams) {
        final ItemStack teamSelector = new ItemStack(material);
        ItemMeta itemMeta = teamSelector.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        teamSelector.setItemMeta(itemMeta);

        TeamSelector.teamSelector = teamSelector;
        TeamSelector.teams = teams;
        return teamSelector;
    }
}
