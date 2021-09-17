package fr.hyriode.hyrame.game;

import fr.hyriode.common.title.Title;
import fr.hyriode.hyrame.Hyrame;
import fr.hyriode.hyrame.language.Language;
import fr.hyriode.hyrame.language.LanguageMessage;
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

    private final LanguageMessage cancelMessage = new LanguageMessage("game.cancel");

    private boolean running;

    private int time = 30;

    private final HyriGame<?> game;
    private final Hyrame hyrame;

    public HyriGameStartingTimer(Hyrame hyrame, HyriGame<?> game) {
        this.hyrame = hyrame;
        this.game = game;

        this.cancelMessage.addValue(Language.FR, ChatColor.RED + "Annulée");
        this.cancelMessage.addValue(Language.EN, ChatColor.RED + "Cancelled");

        this.hyrame.getLanguageManager().addMessage(this.cancelMessage);
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

            if (this.time <= 0) {
                this.game.startGame();
            } else if (this.time <= 3) {
                this.sendTitle(ChatColor.DARK_AQUA + "" + this.time, false);
            } else if (this.time == 30 || this.time == 20 || this.time == 10 || this.time <= 5){
                this.sendTitle(ChatColor.AQUA + "" + this.time, false);
            }

            this.sendSound();

            this.time--;
        } else {
            if (this.running) {
                this.running = false;
                this.time = 60;
                this.game.setState(HyriGameState.WAITING);

                this.sendTitle(this.cancelMessage.getKey(), true);
                this.sendSound();
            }
        }
    }

    private void sendTitle(String title, boolean lang) {
        this.game.getPlayers().forEach(player -> Title.setTitle(player.getPlayer().getPlayer(), lang ? this.hyrame.getLanguageManager().getMessageForPlayer(player.getUuid(), title) : title, "", 0, 20, 20));
    }

    private void sendSound() {
        final boolean ring = this.time <= 5;

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
