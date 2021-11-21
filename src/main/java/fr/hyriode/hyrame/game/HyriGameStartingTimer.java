package fr.hyriode.hyrame.game;

import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import fr.hyriode.tools.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/09/2021 at 21:06
 */
public class HyriGameStartingTimer implements Runnable {

    private static final HyriLanguageMessage CANCEL_MESSAGE = new HyriLanguageMessage("game.cancel")
            .addValue(HyriLanguage.FR, ChatColor.RED + "Annulée")
            .addValue(HyriLanguage.EN, ChatColor.RED + "Cancelled");

    private boolean running;

    private int time = 30;

    private final HyriGame<?> game;

    public HyriGameStartingTimer(HyriGame<?> game) {
        this.game = game;
    }

    @Override
    public void run() {
        final int players = this.game.getPlayers().size();

        if (players >= this.game.getMinPlayers()) {
            this.game.setState(HyriGameState.READY);

            if (!this.running) {
                this.running = true;
            }

            if (players == this.game.getMaxPlayers() && this.time > 15) {
                this.time = 15;
            }

            this.game.getWaitingScoreboards().forEach(scoreboard -> scoreboard.setTime(this.time));

            if (this.time <= 0) {
                this.game.start();
            } else if (this.time <= 3) {
                this.sendTitle(ChatColor.AQUA + "" + this.time, false);
            } else if (this.time == 30 || this.time == 20 || this.time == 10){
                this.sendTitle(ChatColor.DARK_AQUA + "" + this.time, false);
            }

            this.sendSound();

            this.time--;
        } else {
            if (this.running) {
                this.running = false;
                this.time = 60;
                this.game.setState(HyriGameState.WAITING);

                this.game.getWaitingScoreboards().forEach(scoreboard -> scoreboard.setTime(-1));

                this.sendTitle("", true);
                this.sendSound();
            }
        }
    }

    private void sendTitle(String title, boolean lang) {
        this.game.getPlayers().forEach(player -> Title.sendTitle(player.getPlayer().getPlayer(), lang ? CANCEL_MESSAGE.getForPlayer(player.getPlayer().getPlayer()) : title, "", 0, 20, 0));
    }

    private void sendSound() {
        final boolean ring = this.time <= 3;

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.setLevel(this.time);

            if (this.time == 0) {
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1L, 1L);
            } else if (this.time == 30 || this.time == 20 || this.time == 10){
                player.playSound(player.getLocation(), Sound.CLICK, 1L, 1L);
            } else if (ring) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1L, 1L);
            }
        }
    }

}
