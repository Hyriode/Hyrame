package fr.hyriode.hyrame.tablist;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.scoreboard.team.HyriScoreboardTeam;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AstFaster
 * on 30/05/2022 at 19:44
 */
public interface ITabListManager {

    void enable();

    void disable();

    void registerTeam(HyriScoreboardTeam team);

    void unregisterTeam(String teamName);

    void updateTeam(String teamName);

    HyriScoreboardTeam getTeam(String teamName);

    boolean containsTeam(String teamName);

    void addPlayerInTeam(Player player, String teamName);

    void removePlayerFromTeam(Player player);

    HyriScoreboardTeam getPlayerTeam(Player player);

    void hideNameTag(NameTagAction action);

    void showNameTag(NameTagAction action);

    boolean isEnabled();

    class NameTagAction {

        private Player player;
        private List<Player> targets;

        public NameTagAction(Player player, List<Player> targets) {
            this.player = player;
            this.targets = targets;
        }

        public NameTagAction(Player player) {
            this(player, new ArrayList<>());
        }

        public Player getPlayer() {
            return this.player;
        }

        public NameTagAction withPlayer(Player player) {
            if (player == null) {
                throw new IllegalArgumentException("Player cannot be null!");
            }

            this.player = player;
            return this;
        }

        public List<Player> getTargets() {
            return this.targets;
        }

        public NameTagAction setTargets(List<Player> targets) {
            if (targets == null) {
                targets = new ArrayList<>();
            }

            this.targets = targets;
            return this;
        }

        public NameTagAction addTarget(Player player) {
            this.targets.add(player);
            return this;
        }

        public NameTagAction removeTarget(Player player) {
            this.targets.remove(player);
            return this;
        }

        public NameTagAction addTargets(List<Player> targets) {
            this.targets.addAll(targets);
            return this;
        }

        public NameTagAction addTarget(HyriGameTeam gameTeam) {
            for (HyriGamePlayer gamePlayer : gameTeam.getPlayers()) {
                this.targets.add(gamePlayer.getPlayer());
            }
            return this;
        }

        public NameTagAction removeTarget(HyriGameTeam gameTeam) {
            for (HyriGamePlayer gamePlayer : gameTeam.getPlayers()) {
                this.targets.remove(gamePlayer.getPlayer());
            }
            return this;
        }

    }

}