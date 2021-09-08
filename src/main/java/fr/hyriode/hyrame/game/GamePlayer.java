package fr.hyriode.hyrame.game;

import fr.hyriode.hyrame.team.Team;
import fr.hyriode.hyrame.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

public class GamePlayer {

<<<<<<< HEAD:src/main/java/fr/hyriode/hyrame/gamemethods/GamePlayer.java
    private Plugin plugin;
=======
>>>>>>> 03c9801153edce882d7bd69bfb8fd8946e043860:src/main/java/fr/hyriode/hyrame/game/GamePlayer.java
    private Player player;
    private Game game;
    private Location respawnLocation;
    private Boolean canSpeakWhenDead;
    private Location deadLocation;
    private boolean canRespawn;
    private boolean isAlive;
    private int respawnTime;
    private Inventory deathInventory;
    private GamePlayer lastDamager;


    public GamePlayer(Plugin plugin, Player player, Game game) {
        if(GameManager.gamePlayerByPlayer(player) == null) {
            this.plugin = plugin;
            this.player = player;
            this.game = game;
            this.canRespawn = this.game.canRespawn();
            this.respawnTime = this.game.getBaseRespawnTime();
            GameManager.gamePlayersManager.add(this);
            this.game.gamePlayers.add(this);
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public Team getTeam() {
        return TeamManager.getTeamByPlayer(player);
    }

    public boolean canRespawn() {
        return this.canRespawn;
    }

    public void setCanRespawn(boolean canRespawn) {
        this.canRespawn = canRespawn;
    }

    public Location getRespawnLocation() {
        return this.respawnLocation;
    }

    public void setRespawnLocation(Location respawnLocation) {
        this.respawnLocation = respawnLocation;
    }

    public Location getDeadLocation() {
        return this.deadLocation;
    }

    public void setDeadLocation(Location deadLocation) {
        this.deadLocation = deadLocation;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void finalKill(boolean canSpectate, boolean canSpeak) {
        //need to kill the gamePlayer before
        this.game.deadPlayers.add(this);
        if(canSpectate) {
            this.deadLocation = player.getLocation();
        }else {
            //PLACEHOLDER
            this.player.kickPlayer("PLACEHOLDER");
            //PLACEHOLDER
        }
        this.canSpeakWhenDead = canSpeak;
    }

    public boolean revive() {
        if(this.player.isOnline() && this.deadLocation != null) {
            this.player.setGameMode(GameMode.SURVIVAL);
            this.player.setHealth(this.player.getMaxHealth());
            this.player.setNoDamageTicks(30);
            this.player.setFoodLevel(20);
            this.player.setSaturation(20);
            this.player.teleport(this.deadLocation);
            this.isAlive = true;
            return true;
        }else {
            return false;
        }
    }

    public void kill() {
        this.isAlive = false;
        this.player.setGameMode(GameMode.SPECTATOR);
        this.player.teleport(new Location(this.player.getWorld(), 0, 100, 0));

        if(this.getGame().getDeathMethod().equals(DeathMethods.KEEP_INVENTORY)) {
            this.setDeathInventory(this.player.getInventory());
            this.player.getInventory().clear();
        }else if(this.getGame().getDeathMethod().equals(DeathMethods.DESTROY_STUFF)) {
            this.player.getInventory().clear();
        }else if(this.getGame().getDeathMethod().equals(DeathMethods.DROP_AT_DEATH_LOCATION)) {
            for (ItemStack itemStack : this.player.getInventory().getContents()) {
                this.player.getWorld().dropItemNaturally(this.player.getLocation(), itemStack);
                this.player.getInventory().removeItem(itemStack);
            }
            for (ItemStack itemStack : this.player.getInventory().getArmorContents()) {
                this.player.getWorld().dropItemNaturally(this.player.getLocation(), itemStack);
                this.player.getInventory().removeItem(itemStack);
            }
        }else if(this.getGame().getDeathMethod().equals(DeathMethods.GIVE_STUFF_TO_KILLER)) {
            for (ItemStack itemStack : this.player.getInventory().getContents()) {
                if(this.lastDamager.player.getInventory().getContents().length < this.lastDamager.player.getInventory().getSize()) {
                    this.lastDamager.player.getInventory().addItem(itemStack);
                }else {
                    this.lastDamager.player.getWorld().dropItemNaturally(this.lastDamager.player.getLocation(), itemStack);
                }
                this.player.getInventory().removeItem(itemStack);
            }
            for (ItemStack itemStack : this.player.getInventory().getArmorContents()) {
                if(this.lastDamager.player.getInventory().getContents().length < this.lastDamager.player.getInventory().getSize()) {
                    this.lastDamager.player.getInventory().addItem(itemStack);
                }else {
                    this.lastDamager.player.getWorld().dropItemNaturally(this.lastDamager.player.getLocation(), itemStack);
                }
                this.player.getInventory().removeItem(itemStack);
            }
        }else if(this.getGame().getDeathMethod().equals(DeathMethods.DROP_AT_KILLER_LOCATION)) {
            for (ItemStack itemStack : this.player.getInventory().getContents()) {
                this.lastDamager.player.getWorld().dropItemNaturally(this.lastDamager.player.getLocation(), itemStack);
                this.player.getInventory().removeItem(itemStack);
            }
            for (ItemStack itemStack : this.player.getInventory().getArmorContents()) {
                this.lastDamager.player.getWorld().dropItemNaturally(this.lastDamager.player.getLocation(), itemStack);
                this.player.getInventory().removeItem(itemStack);
            }
        }
        if(this.canRespawn()) {
<<<<<<< HEAD:src/main/java/fr/hyriode/hyrame/gamemethods/GamePlayer.java
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, this.respawn(), this.getRespawnTime() * 20L);
=======
            //Bukkit.getScheduler().scheduleSyncDelayedTask(hyrame, this.respawn(), this.getRespawnTime() * 20L);
>>>>>>> 03c9801153edce882d7bd69bfb8fd8946e043860:src/main/java/fr/hyriode/hyrame/game/GamePlayer.java
        }else {
            this.finalKill(this.getGame().canPlayersSpectateDead(), this.getGame().canPlayersSpeakDead());
        }
    }

    public Runnable respawn() {
        this.isAlive = true;
        this.player.setGameMode(GameMode.SURVIVAL);
        this.player.setHealth(this.player.getMaxHealth());
        this.player.setNoDamageTicks(30);
        this.player.setFoodLevel(20);
        this.player.setSaturation(20);
        this.player.teleport(respawnLocation);
        if(this.deathInventory != null) {
            for(ItemStack itemStack : deathInventory.getContents()) {
                if(itemStack != null) {
                    this.player.getInventory().addItem(itemStack);
                }
            }
        }
        return null;
    }

    public boolean isFinalKilled() {
        return !this.canRespawn && !this.isAlive;
    }

    public Game getGame() {
        return game;
    }

    public Boolean getCanSpeakWhenDead() {
        return canSpeakWhenDead;
    }

    public void setCanSpeakWhenDead(Boolean canSpeakWhenDead) {
        this.canSpeakWhenDead = canSpeakWhenDead;
    }

    public void setDeathInventory(PlayerInventory deathInventory) {
        this.deathInventory = deathInventory;
    }

    public int getRespawnTime() {
        return respawnTime;
    }

    public void setRespawnTime(int respawnTime) {
        this.respawnTime = respawnTime;
    }

    public GamePlayer getLastDamager() {
        return lastDamager;
    }

    public void setLastDamager(GamePlayer lastDamager) {
        this.lastDamager = lastDamager;
    }
}

