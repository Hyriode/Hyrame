package fr.hyriode.hyrame.gameMethods;

import fr.hyriode.hyrame.Hyrame;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class GamePlayerManager {


    public static ArrayList<GamePlayer> gamePlayersManager = new ArrayList<GamePlayer>();

    public static GamePlayer gamePlayerByPlayer(Player player) {
        GamePlayer playerGamePlayer = null;
        for(GamePlayer gamePlayer : gamePlayersManager) {
            if(gamePlayer.getPlayer().equals(player)) {
                playerGamePlayer = gamePlayer;
                break;
            }
        }
        return playerGamePlayer;
    }
}
