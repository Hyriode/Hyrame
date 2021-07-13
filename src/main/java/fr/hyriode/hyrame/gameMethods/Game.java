package fr.hyriode.hyrame.gameMethods;

import fr.hyriode.hyrame.team.TeamManager;
import org.bukkit.Location;

import java.util.ArrayList;

public class Game {

    public ArrayList<GamePlayer> gamePlayers = new ArrayList<GamePlayer>();
    public ArrayList<GamePlayer> deadPlayers = new ArrayList<GamePlayer>();

    private DeathMethods deathMethod;
    private final String gameName;
    private boolean canPlayersSpeakDead;
    private boolean canPlayersSpectateDead;
    private boolean baseCanRespawn;
    private int baseRespawnTime;

    public Game(DeathMethods deathMethod, String gameName, boolean canPlayersSpeakDead, boolean canPlayersSpectateDead, boolean baseCanRespawn, int baseRespawnTime) {
         this.deathMethod = deathMethod;
         this.gameName = gameName;
         this.canPlayersSpeakDead = canPlayersSpeakDead;
         this.canPlayersSpectateDead = canPlayersSpectateDead;
         this.baseCanRespawn = baseCanRespawn;
         this.baseRespawnTime = baseRespawnTime;
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
