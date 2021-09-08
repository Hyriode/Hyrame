package fr.hyriode.hyrame.game;

import fr.hyriode.hyrame.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;

public class Game {

    public ArrayList<Team> teams = new ArrayList<>();
    public ArrayList<GamePlayer> gamePlayers = new ArrayList<>();
    public ArrayList<GamePlayer> deadPlayers = new ArrayList<>();

    private DeathMethods deathMethod;
    private String gameName;
    private boolean canPlayersSpeakDead;
    private boolean canPlayersSpectateDead;
    private boolean baseCanRespawn;
    private int baseRespawnTime;
    public boolean isNoGameTeam;

    public Game(boolean isNoGameTeam, DeathMethods deathMethod, String gameName, boolean canPlayersSpeakDead, boolean canPlayersSpectateDead, boolean baseCanRespawn, int baseRespawnTime) {
        if(GameManager.getGameByName(gameName) == null) {
            this.isNoGameTeam = isNoGameTeam;
            this.deathMethod = deathMethod;
            this.gameName = gameName;
            this.canPlayersSpeakDead = canPlayersSpeakDead;
            this.canPlayersSpectateDead = canPlayersSpectateDead;
            this.baseCanRespawn = baseCanRespawn;
            this.baseRespawnTime = baseRespawnTime;
            GameManager.games.add(this);
        }else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "The game " + gameName + "cannot be created because a game already exist");
        }
    }


    public DeathMethods getDeathMethod() {
        return deathMethod;
    }

    public void setDeathMethods(DeathMethods deathMethods) {
        this.deathMethod = deathMethods;
    }

    public String getGameName() {
        return gameName;
    }

    public boolean canPlayersSpeakDead() {
        return canPlayersSpeakDead;
    }

    public void setCanPlayersSpeakDead(boolean canPlayersSpeakDead) {
        this.canPlayersSpeakDead = canPlayersSpeakDead;
    }

    public boolean canPlayersSpectateDead() {
        return canPlayersSpectateDead;
    }

    public void setCanPlayersSpectateDead(boolean canPlayersSpectateDead) {
        this.canPlayersSpectateDead = canPlayersSpectateDead;
    }

    public boolean canRespawn() {
        return baseCanRespawn;
    }

    public void setCanRespawn(boolean baseCanRespawn) {
        this.baseCanRespawn = baseCanRespawn;
    }

    public int getBaseRespawnTime() {
        return baseRespawnTime;
    }

    public void setBaseRespawnTime(int baseRespawnTime) {
        this.baseRespawnTime = baseRespawnTime;
    }



    /*public void startGame(int godTime, ArrayList<Location> spawnLocations, boolean spawnInTeams, boolean setRespawnLocationToSpawnLocation) {
        if(spawnInTeams) {
            if(spawnLocations.size() >= TeamManager.teamManager.size()) {
                for(GamePlayer gamePlayer : GamePlayerManager.gamePlayersManager) {
                    gamePlayer.getPlayer().teleport(spawnLocations.get(GamePlayerManager.gamePlayersManager.indexOf(gamePlayer)));
                }
            }else {
                
            }
        }
    }

    public void pulseAction(int pulseTime,Runnable runAfterPulse) {

    }*/
}
