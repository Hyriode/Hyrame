package fr.hyriode.hyrame.team;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class Team extends ArrayList {

    private TeamColor teamColor;
    private ArrayList<Player> members;
    private int maxSize;
    private boolean friendlyFire;

    public Team(TeamColor teamColor, ArrayList<Player> members, int maxSize, boolean friendlyFire) {
        if(members != null) {
            for(Player player : members) {
                if(TeamManager.getTeamByPlayer(player) != null) {
                    TeamManager.getTeamByPlayer(player).remove(player);
                }
            }
            if(members.size() <= maxSize) {
                this.teamColor = teamColor;
                this.members = members;
                this.maxSize = maxSize;
                this.friendlyFire = friendlyFire;
                Bukkit.getConsoleSender().sendMessage("New team created : " + teamColor.toString() + ", " + members+ ", " + maxSize + ", " + friendlyFire);
                TeamManager.teamManager.add(this);
            }else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error, number of members can't be superior to max size");
            }
        }else {
            this.teamColor = teamColor;
            this.members = new ArrayList<Player>();
            this.maxSize = maxSize;
            this.friendlyFire = friendlyFire;
            Bukkit.getConsoleSender().sendMessage("New team created : " + teamColor.toString() + ", " + "any member" + ", " + maxSize + ", " + friendlyFire);
            TeamManager.teamManager.add(this);
        }
    }

    public Boolean getFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(Boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public TeamColor getTeamColor() {
        return teamColor;
    }

    public void setTeamColor(TeamColor teamColor) {
        this.teamColor = teamColor;
    }


    public ArrayList<Player> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<Player> members) {
        this.members = members;
    }


    public boolean addMember(Player member) {
        Team team = TeamManager.getTeamByPlayer(member);
        if(team != null) {
            team.remove(member);
        }
        if(!this.members.contains(member) || members.size() + 1 > maxSize) {
            this.members.add(member);
            Bukkit.getConsoleSender().sendMessage("Player " + member.getName() + " was added to " + this.teamColor.toString());
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

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void killAll() {
        for(Player member : this.members) {
            member.damage(100);
        }
    }

    public void kickAll(String reason) {
        for(Player member : this.members) {
            member.kickPlayer(reason);
        }
    }

    public void tpAll(Location location) {
        for(Player member : this.members) {
            member.teleport(location);
        }
    }

    public void spreadAll(Location center, int rayon) {
        for(Player member : this.members) {
            final double x = center.getX() + new Random().nextInt(rayon*2) - rayon/2;
            final double z = center.getZ() + new Random().nextInt(rayon*2) - rayon/2;
            final double y =  member.getWorld().getHighestBlockAt((int)x, (int)z).getY();
            final Location location = new Location(member.getWorld(), x, y, z);

            member.teleport(location);
        }
    }

    public void giveAll(ItemStack itemStack) {
        for(Player member : this.members) {
            member.getInventory().addItem(itemStack);
        }
    }

}
