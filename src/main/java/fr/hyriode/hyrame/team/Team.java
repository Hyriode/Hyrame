package fr.hyriode.hyrame.team;

import fr.hyriode.hyrame.game.Game;
import fr.hyriode.hyrame.game.GameManager;
import fr.hyriode.hyrame.game.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class Team {

    private Game game;
    private TeamColor teamColor;
    private ArrayList<GamePlayer> members;
    private int maxSize;
    private boolean friendlyFire;

    public Team(Game game, TeamColor teamColor, ArrayList<Player> members, int maxSize, boolean friendlyFire) {
        if(members != null && !game.isNoGameTeam) {
            for(Player player : members) {
                if(TeamManager.getTeamByPlayer(player) != null) {
                    TeamManager.getTeamByPlayer(player).getMembers().remove(player);
                }
            }
            if(members.size() <= maxSize) {
                this.game = game;
                this.teamColor = teamColor;
                this.members = GameManager.createGamePlayers(members, game);
                this.maxSize = maxSize;
                this.friendlyFire = friendlyFire;
                Bukkit.getConsoleSender().sendMessage("New team created : " + teamColor.toString() + ", " + members+ ", " + maxSize + ", " + friendlyFire);
                TeamManager.teamManager.add(this);
            }else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error the team " + teamColor.toString() + "of " + game.getGameName() + " because the number of members is superior to max size or the game " + game.getGameName() + " is a noGameTeam");
            }
        }else {
            this.teamColor = teamColor;
            this.members = new ArrayList<>();
            this.maxSize = maxSize;
            this.friendlyFire = friendlyFire;
            Bukkit.getConsoleSender().sendMessage("New team created : " + teamColor.toString() + ", " + "any member" + ", " + maxSize + ", " + friendlyFire);
            TeamManager.teamManager.add(this);
        }
    }

    public Boolean getFriendlyFire() {
        return this.friendlyFire;
    }

    public void setFriendlyFire(Boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public TeamColor getTeamColor() {
        return this.teamColor;
    }

    public ArrayList<Player> getMembers() {
        ArrayList<Player> players = new ArrayList<>();
        for (GamePlayer gamePlayer : members) {
            players.add(gamePlayer.getPlayer());
        }
        return players;
    }

    public void setMembers(ArrayList<Player> members) {
        for(Player player : members) {
            if(TeamManager.getTeamByPlayer(player) != null) {
                TeamManager.getTeamByPlayer(player).getMembers().remove(player);
            }
            if(GameManager.gamePlayerByPlayer(player) == null) {
                this.members.add(new GamePlayer(player, game));
            }else {
                this.members.add(GameManager.gamePlayerByPlayer(player));
            }
        }
    }

    public boolean addMember(Player member) {
        Team team = TeamManager.getTeamByPlayer(member);
        if(team != null) {
            team.getMembers().remove(member);
        }
        if(!this.members.contains(member) && this.members.size() + 1 <= maxSize) {
            if(GameManager.gamePlayerByPlayer(member) == null) {
                this.members.add(new GamePlayer(member, game));
            }else {
                this.members.add(GameManager.gamePlayerByPlayer(member));
            }
            Bukkit.getConsoleSender().sendMessage("Player " + member.getName() + " has been added to " + this.teamColor.toString());
            return true;
        }else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error, player " + member.getName() + " is aldrealdy in the team or the team is full");
            return false;
        }
    }

    public void removeMember(Player member) {
        if(this.members.contains(member)) {
            this.members.remove(member);
        }else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error, the player " + member.getName() + "isn't in the team");
        }
    }

    public void clearMembers() {
        this.members.clear();
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void tpAll(Location location) {
        for(GamePlayer member : this.members) {
            member.getPlayer().teleport(location);
        }
    }

    public void giveAll(ItemStack itemStack) {
        for(GamePlayer member : this.members) {
            member.getPlayer().getInventory().addItem(itemStack);
        }
    }

    public Boolean isAlive() {
        boolean isAlive = false;
        for(GamePlayer member : this.members) {
            if(member.isAlive()) {
                isAlive = true;
                break;
            }
        }
        return isAlive;
    }

    public Player pickRandomMember() {
        if(members != null && !members.isEmpty()) {
            Random random = new Random();
            return members.get(random.nextInt(members.size())).getPlayer();
        }else {
            return null;
        }
    }
}
