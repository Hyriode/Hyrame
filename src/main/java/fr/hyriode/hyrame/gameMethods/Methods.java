package fr.hyriode.hyrame.gameMethods;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Methods {

    public void onPlayerJoinGame(Player player, Location spawnLocation) {
        player.setHealthScale(20);
        player.setHealth(20);
        player.setCanPickupItems(false);
        player.setAllowFlight(false);
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(spawnLocation);
        player.setLevel(0);
        player.getInventory().clear();
        player.setTotalExperience(0);
        player.setExp(0);
    }
}
