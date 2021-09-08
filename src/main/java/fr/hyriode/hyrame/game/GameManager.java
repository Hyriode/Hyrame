package fr.hyriode.hyrame.game;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class GameManager {

    public static ArrayList<Game> games = new ArrayList<>();

    public static Game getGameByName(String gameName) {
        Game game = null;
        for(Game game1 : games) {
            if(game1.getGameName().equals(gameName)) {
                game = game1;
                break;
            }
        }
        return game;
    }

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

    public static ArrayList<GamePlayer> createGamePlayers(Plugin plugin, ArrayList<Player> players, Game game) {
        ArrayList<GamePlayer> gamePlayers = new ArrayList<>();
        for(Player player : players) {
            gamePlayers.add(new GamePlayer(player, game));
        }
        return gamePlayers;
    }
}
