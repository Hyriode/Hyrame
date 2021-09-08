package fr.hyriode.hyrame.game;

import fr.hyriode.hyrame.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {

    private String[] spawnpointsExamples = new String[] {
        "0\n100\n0\n0\n0",
        "100\n100\n0\n0\n0",
        "0\n100\n100\n0\n0"
    };


    private ArrayList<Team> teams = new ArrayList<>();
    public ArrayList<GamePlayer> gamePlayers = new ArrayList<>();
    public ArrayList<GamePlayer> deadPlayers = new ArrayList<>();

    private Plugin plugin;
    private File pluginDataFolder = new File(plugin.getName());
    private DeathMethods deathMethod;
    private String gameName;
    private boolean canPlayersSpeakDead;
    private boolean canPlayersSpectateDead;
    private boolean baseCanRespawn;
    private int baseRespawnTime;
    private int maxPlayers;
    private int minPlayers;
    public boolean isNoTeamGame;


<<<<<<< HEAD:src/main/java/fr/hyriode/hyrame/gamemethods/Game.java
    public Game(Plugin plugin, boolean isNoGameTeam, DeathMethods deathMethod, String gameName, boolean canPlayersSpeakDead, boolean canPlayersSpectateDead, boolean baseCanRespawn, int baseRespawnTime, int minPlayers, int maxPlayers) {

=======
    public Game(boolean isNoGameTeam, DeathMethods deathMethod, String gameName, boolean canPlayersSpeakDead, boolean canPlayersSpectateDead, boolean baseCanRespawn, int baseRespawnTime) {
>>>>>>> 03c9801153edce882d7bd69bfb8fd8946e043860:src/main/java/fr/hyriode/hyrame/game/Game.java
        if(GameManager.getGameByName(gameName) == null) {
            this.maxPlayers = maxPlayers;
            this.minPlayers = minPlayers;
            this.plugin = plugin;
            this.isNoTeamGame = isNoGameTeam;
            this.deathMethod = deathMethod;
            this.gameName = gameName;
            this.canPlayersSpeakDead = canPlayersSpeakDead;
            this.canPlayersSpectateDead = canPlayersSpectateDead;
            this.baseCanRespawn = baseCanRespawn;
            this.baseRespawnTime = baseRespawnTime;
            GameManager.games.add(this);
            createGameConfig(gameName);
        }else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "The game " + gameName + "cannot be created because a game already exist");
        }
    }

    private File createGameConfig(String gameName) {
        this.pluginDataFolder = this.plugin.getDataFolder();

        final File file_config = new File(pluginDataFolder, "games/" + gameName + "_config.yml");

        final YamlConfiguration configuration_config = YamlConfiguration.loadConfiguration(file_config);
        configuration_config.set("spawn.spawnpoints", spawnpointsExamples);

        try {
            configuration_config.save(file_config);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "The game " + gameName + " and its configuration have been created");

        return file_config;
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

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public Player pickRandomPlayer() {
        if(!this.gamePlayers.isEmpty()) {
            Random random = new Random();
            return this.gamePlayers.get(random.nextInt(this.gamePlayers.size())).getPlayer();
        }
        return null;
    }

    public Team pickRandomTeam() {
        if(!this.teams.isEmpty() && !this.isNoTeamGame) {
            Random random = new Random();
            return this.teams.get(random.nextInt(this.teams.size()));
        }else {
            return null;
        }
    }

    public void addTeam(Team team) {
        if(!this.isNoTeamGame) {
            this.teams.add(team);
        }else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "The team " + team.getTeamColor().toString() + " cannot be added to the game " + this.gameName + " because the game is a noTeamGame");
        }
    }

    public void removeTeam(Team team) {
        if(!this.isNoTeamGame) {
            if(this.teams.contains(team)) {
                this.teams.remove(team);
            }else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "The team " + team.getTeamColor().toString() + " cannot be remove to the game " + this.gameName + " because the game does not contain this team");
            }
        }else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "The team " + team.getTeamColor().toString() + " cannot be added to the game " + this.gameName + " because the game is a noTeamGame");
        }
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
    }

    public void start() {

        //PLACEHOLDER
        ConfigurationSection configurationSection = null;
        //PLACEHOLDER

        final List<String> spawnpointsConfigFormat = configurationSection.getStringList("spawnpoints");

        ArrayList<Location> spawnpoints = createLocationByConfigFormat(spawnpointsConfigFormat);

        if(spawnpoints.size() == this.teams.size()) {
            int i = -1;
            for(Team team : this.teams) {
                i++;
                for(Player player : team.getMembers()) {
                    player.teleport(spawnpoints.get(i));
                }
            }
        }else if(spawnpoints.size() == this.maxPlayers) {
            int i = -1;
            for(GamePlayer gamePlayer : this.gamePlayers) {
                i++;
                gamePlayer.getPlayer().teleport(spawnpoints.get(i));
            }

        }else if(spawnpoints.size() > this.maxPlayers) {
            Random random = new Random();
            int i = random.ints(-1, spawnpoints.size() - this.gamePlayers.size()).findFirst().getAsInt();
            for(GamePlayer gamePlayer : this.gamePlayers) {
                i++;
                gamePlayer.getPlayer().teleport(spawnpoints.get(i));
            }
        }else if(spawnpoints.size() > this.teams.size()) {
            Random random = new Random();
            int i = random.ints(-1, spawnpoints.size() - this.gamePlayers.size()).findFirst().getAsInt();
            for(Team team : this.teams) {
                i++;
                for(Player player : team.getMembers()) {
                    player.teleport(spawnpoints.get(i));
                }

            }
        }else if(spawnpoints.size() == 1) {
            for(GamePlayer gamePlayer : this.gamePlayers) {
                gamePlayer.getPlayer().teleport(spawnpoints.get(0));
            }
        }else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Players cannot spawn, the number of spawn points must be greater or equal to the maxPlayer or the number of the team or equal to one");
        }
    }


    private ArrayList<Location> createLocationByConfigFormat(List<String> locations) {
        final ArrayList<Location> locations1 = new ArrayList<>();
        final World world = this.gamePlayers.get(0).getPlayer().getWorld();
        for(String location : locations) {
            String[] parts = location.split("\n");
            locations1.add(new Location(world, Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Float.parseFloat(parts[3]), Float.parseFloat(parts[4])));
        }
        return locations1;
    }



}
