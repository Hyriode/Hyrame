package fr.hyriode.hyrame.scoreboard.team;

import fr.hyriode.hyrame.packet.PacketUtil;
import fr.hyriode.hyrame.reflection.Reflection;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class HyriScoreboardTeamHandler {

    /** Teams */
    private final List<HyriScoreboardTeam> teams;
    /** Receivers */
    private final List<OfflinePlayer> receivers;

    /**
     * Constructor of {@link HyriScoreboardTeamHandler}
     */
    public HyriScoreboardTeamHandler() {
        this.teams = new ArrayList<>();
        this.receivers = new ArrayList<>();
    }

    /**
     * Add a receiver to the handler
     *
     * @param player - Player to add
     * @return - <code>true</code> if it was successful
     */
    public boolean addReceiver(OfflinePlayer player) {
        if (player.isOnline()) {
            this.removeFromAllTeams(player);

            this.receivers.add(player);

            this.sendAllTeams(player.getPlayer());

            return true;
        }
        return false;
    }

    /**
     * Remove a receiver to the handler
     *
     * @param player - Player to remove
     * @return - <code>true</code> if it was successful
     */
    public boolean removeReceiver(OfflinePlayer player) {
        this.receivers.remove(player);
        this.removeFromAllTeams(player);

        if (player.isOnline()) {
            this.removeAllTeams(player.getPlayer());
        }
        return true;
    }

    /**
     * Add a player to a given team
     *
     * @param player - Player
     * @param team - Player's team
     */
    public void addPlayerToTeam(OfflinePlayer player, HyriScoreboardTeam team) {
        this.addPlayerToTeam(player.getName(), team);
    }

    /**
     * Add a player to a given team
     *
     * @param player - Player name
     * @param team - Player's team
     */
    public void addPlayerToTeam(String player, HyriScoreboardTeam team) {
        this.removeFromAllTeams(player);

        team.addPlayer(player);

        for (OfflinePlayer receiver : this.receivers) {
            if (receiver.isOnline()) {
                ScoreboardTeamPacket.addPlayerToTeam(receiver.getPlayer(), team, player);
            }
        }
    }

    /**
     * Remove a player from a team
     *
     * @param player - Player to remove
     * @param team - Player's team
     */
    public void removePlayerFromTeam(String player, HyriScoreboardTeam team) {
        team.removePlayer(player);

        for (OfflinePlayer receiver : this.receivers) {
            if (receiver.isOnline()) {
                ScoreboardTeamPacket.removePlayerFromTeam(receiver.getPlayer(), team, player);
            }
        }
    }

    /**
     * Remove a player from a team
     *
     * @param player - Player to remove
     * @param team - Player's team
     */
    public void removePlayerFromTeam(OfflinePlayer player, HyriScoreboardTeam team) {
        this.removePlayerFromTeam(player.getName(), team);
    }

    /**
     * Add a team
     *
     * @param team - Team to add
     */
    public void addTeam(HyriScoreboardTeam team) {
        this.teams.add(team);

        this.sendTeamToAllPlayers(team);
    }

    /**
     * Remove a team
     *
     * @param name - Team's name
     * @return - <code>true</code> if it was successful
     */
    public boolean removeTeam(String name) {
        final HyriScoreboardTeam team = this.getTeamByName(name);

        if (team != null) {
            this.removeTeamFromAllPlayers(team);

            return true;
        }
        return false;
    }

    /**
     * Update a given team
     *
     * @param name The name of the team
     * @return <code>true</code> if it was successful
     */
    public boolean updateTeam(String name) {
        final HyriScoreboardTeam team = this.getTeamByName(name);

        if (team != null) {
            for (OfflinePlayer player : this.receivers) {
                if (player.isOnline()) {
                    ScoreboardTeamPacket.updateTeam(player.getPlayer(), team);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Remove a player from all teams
     *
     * @param player - Player
     */
    private void removeFromAllTeams(String player) {
        for (HyriScoreboardTeam team : this.teams) {
            for (String p : team.getPlayers()) {
                if (player.equals(p)) {
                    for (OfflinePlayer receiver : this.receivers) {
                        if (receiver.isOnline()) {
                            ScoreboardTeamPacket.removePlayerFromTeam(receiver.getPlayer(), team, player);
                        }
                    }

                    team.removePlayer(player);
                }
            }
        }
    }

    /**
     * Remove a player from all teams
     *
     * @param player - Player
     */
    private void removeFromAllTeams(OfflinePlayer player) {
        this.removeFromAllTeams(player.getName());
    }

    private void sendTeam(Player player, HyriScoreboardTeam team) {
        ScoreboardTeamPacket.createTeam(player, team);
        ScoreboardTeamPacket.sendTeam(player, team);
    }

    private void removeTeam(Player player, HyriScoreboardTeam team) {
        ScoreboardTeamPacket.removeTeam(player, team);
    }

    private void sendAllTeams(Player player) {
        for (HyriScoreboardTeam team : this.teams) {
            this.sendTeam(player, team);
        }
    }

    private void removeAllTeams(Player player) {
        for (HyriScoreboardTeam team : this.teams) {
            this.removeTeam(player, team);
        }
    }

    private void sendTeamToAllPlayers(HyriScoreboardTeam team) {
        for (OfflinePlayer player : this.receivers) {
            if (player.isOnline()) {
                this.sendTeam(player.getPlayer(), team);
            }
        }
    }

    private void removeTeamFromAllPlayers(HyriScoreboardTeam team) {
        for (OfflinePlayer player : this.receivers) {
            if (player.isOnline()) {
                this.removeTeam(player.getPlayer(), team);
            }
        }
    }

    /**
     * Get a team by its name
     *
     * @param name - Team's name
     * @return - The team
     */
    public HyriScoreboardTeam getTeamByName(String name) {
        for (HyriScoreboardTeam team : this.teams) {
            if (team.getName().equals(name)) {
                return team;
            }
        }
        return null;
    }

    /**
     * Get the team of a player
     *
     * @param player - Player
     * @return - The player's team
     */
    public HyriScoreboardTeam getPlayerTeam(Player player) {
        for (HyriScoreboardTeam team : this.teams) {
            team.contains(player.getName());
        }
        return null;
    }

    private static class ScoreboardTeamPacket {

        public static void createTeam(Player player, HyriScoreboardTeam team) {
            PacketUtil.sendPacket(player, getPacket(team, 0));
        }

        public static void removeTeam(Player player, HyriScoreboardTeam team) {
            PacketUtil.sendPacket(player, getPacket(team, 1));
        }

        public static void updateTeam(Player player, HyriScoreboardTeam team) {
            PacketUtil.sendPacket(player, getPacket(team, 2));
        }

        public static void sendTeam(Player player, HyriScoreboardTeam team) {
            PacketUtil.sendPacket(player, getPacket(team, 3));
        }

        public static void addPlayerToTeam(Player receiver, HyriScoreboardTeam team, String player) {
            PacketUtil.sendPacket(receiver, getPacket(team, Collections.singletonList(player), 3));
        }

        public static void addPlayerToTeam(Player receiver, HyriScoreboardTeam team, Player player) {
            addPlayerToTeam(receiver, team, player.getName());
        }

        public static void removePlayerFromTeam(Player receiver, HyriScoreboardTeam team, String player) {
            if (team.getPlayers().contains(player)) {
                PacketUtil.sendPacket(receiver, getPacket(team, Collections.singletonList(player), 4));
            }
        }

        public static void removePlayerFromTeam(Player receiver, HyriScoreboardTeam team, Player player) {
            removePlayerFromTeam(receiver, team, player.getName());
        }

        private static Packet<?> getPacket(HyriScoreboardTeam team, List<String> newPlayers, int mode) {
            final PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();

            if (newPlayers == null) {
                newPlayers = new ArrayList<>();
            }

            for (String player : newPlayers) {
                System.out.println("Adding: " + player);
            }

            Reflection.setField("a", packet, team.getRealName());
            Reflection.setField("b", packet, team.getDisplay());
            Reflection.setField("c", packet, team.getPrefix());
            Reflection.setField("d", packet, team.getSuffix());
            Reflection.setField("e", packet, team.getNameTagVisibility().toString());
            Reflection.setField("f", packet, newPlayers.size());
            Reflection.setField("g", packet, newPlayers);
            Reflection.setField("h", packet, mode);
            Reflection.setField("i", packet, 0);

            return packet;
        }

        private static Packet<?> getPacket(HyriScoreboardTeam team, int mode) {
            return getPacket(team, team.getPlayers(), mode);
        }

    }

}
