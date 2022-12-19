package fr.hyriode.hyrame.game.timer;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguage;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/09/2021 at 21:06
 */
public class HyriGameStartingTimer extends BukkitRunnable {

    private Consumer<Integer> timeChanged;

    private boolean running;
    private boolean forceStarting;

    private int time = 30;

    private final JavaPlugin plugin;
    private final HyriGame<?> game;

    public HyriGameStartingTimer(JavaPlugin plugin, HyriGame<?> game) {
        this.plugin = plugin;
        this.game = game;
    }

    @Override
    public void run() {
        if ((this.game.canStart() && HyriAPI.get().getServer().getAccessibility() != HyggServer.Accessibility.HOST) || this.forceStarting) {
            this.start();
        } else {
            this.runActionOnPlayers(gamePlayer -> gamePlayer.getPlayer().setLevel(0));

            this.cancel(false);
        }
    }

    private void start() {
        this.game.setState(HyriGameState.READY);

        if (!this.running) {
            this.running = true;
        }

        if (this.game.getPlayers().size() == HyriAPI.get().getServer().getSlots() && this.time > 15) {
            this.time = 15;
        }

        this.runActionOnPlayers(gamePlayer -> gamePlayer.getPlayer().setLevel(this.time));
        this.onTimeChanged(this.time);

        if (this.time <= 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    game.start();
                }
            }.runTask(this.plugin);
        } else if (this.time <= 3) {
            this.sendTitle(target -> ChatColor.AQUA + "" + this.time);
        } else if (this.time == 30 || this.time == 20 || this.time == 10){
            this.sendTitle(target -> ChatColor.DARK_AQUA + "" + this.time);
        }

        this.sendSound();

        this.time--;
    }

    public void forceStarting() {
        this.time = 10;
        this.forceStarting = true;

        this.start();
    }

    public void cancel(boolean force) {
        if (this.running && (HyriAPI.get().getServer().getAccessibility() != HyggServer.Accessibility.HOST) || force) {
            this.forceStarting = false;
            this.running = false;
            this.time = -1;

            this.onTimeChanged(-1);

            this.game.setState(HyriGameState.WAITING);

            this.sendTitle(HyrameMessage.GAME_STARTING_CANCELLED::asString);
            this.sendSound();

            this.runActionOnPlayers(gamePlayer -> gamePlayer.getPlayer().setLevel(0));
        }
    }

    private void runActionOnPlayers(Consumer<HyriGamePlayer> action) {
        this.game.getPlayers().forEach(action);
    }

    private void onTimeChanged(int time) {
        if (this.timeChanged != null) {
            this.timeChanged.accept(time);
        }
    }

    private void sendTitle(Function<Player, String> title) {
        this.game.getPlayers().forEach(gamePlayer -> {
            final Player player = gamePlayer.getPlayer();

            Title.sendTitle(player, title.apply(player), "", 0, 20, 0);
        });
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

    public void setOnTimeChanged(Consumer<Integer> timeChanged) {
        this.timeChanged = timeChanged;
    }

    public boolean isRunning() {
        return this.running;
    }

}
