package fr.hyriode.hyrame.game.protocol;

import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.language.HyriCommonMessages;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.reflection.Reflection;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.PlayerUtil;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.hyrame.utils.ThreadUtil;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 24/02/2022 at 19:53
 */
public class HyriDeathProtocol extends HyriGameProtocol implements Listener {

    private static final HyriLanguageMessage DEAD = new HyriLanguageMessage("")
            .addValue(HyriLanguage.EN, "DEAD")
            .addValue(HyriLanguage.FR, "MORT");

    private static final HyriLanguageMessage RESPAWN = new HyriLanguageMessage("")
            .addValue(HyriLanguage.EN, "Respawn in")
            .addValue(HyriLanguage.FR, "Réapparition dans");

    /** A list of players used to cancel double deaths */
    private final List<Player> deadPlayers = new ArrayList<>();

    /** The options of the protocol */
    private Options options;

    /** The {@link JavaPlugin} instance */
    private final JavaPlugin plugin;
    /** The {@link Predicate} tested before continuing normal death protocol or switch spectator protocol */
    private final Predicate<HyriGamePlayer> continueDeath;
    /** The screen that will be shown on death */
    private final Screen screen;
    /** The class of the screen handler */
    private final Class<? extends ScreenHandler> screenHandler;

    /**
     * Constructor of {@link HyriDeathProtocol}
     *
     * @param hyrame The {@link IHyrame} instance
     * @param plugin A {@link JavaPlugin}
     * @param continueDeath The {@link Predicate} to test after death
     * @param screen The screen object
     * @param screenHandler The screen handler class
     */
    public HyriDeathProtocol(IHyrame hyrame, JavaPlugin plugin, Predicate<HyriGamePlayer> continueDeath, Screen screen, Class<? extends ScreenHandler> screenHandler) {
        super(hyrame, "death");
        this.plugin = plugin;
        this.continueDeath = continueDeath;
        this.screen = screen;
        this.screenHandler = screenHandler;
        this.options = new Options();

        if (this.screen != null) {
            final Consumer<Player> realCallback = this.screen.getCallback();

            this.screen.callback = p -> {
                final HyriGamePlayer gPlayer = this.getGamePlayer(p);

                gPlayer.setDead(false);
                gPlayer.show();

                p.setAllowFlight(false);
                p.setFlying(false);

                PlayerUtil.resetPotionEffects(p);

                realCallback.accept(p);
            };
        }

        this.require(HyriLastHitterProtocol.class);
    }

    /**
     * Constructor of {@link HyriDeathProtocol}
     *
     * @param hyrame The {@link IHyrame} instance
     * @param plugin A {@link JavaPlugin}
     * @param continueDeath The {@link Predicate} to test after death
     */
    public HyriDeathProtocol(IHyrame hyrame, JavaPlugin plugin, Predicate<HyriGamePlayer> continueDeath) {
        this(hyrame, plugin, continueDeath, null, null);
    }

    @Override
    void enable() {}

    @Override
    void disable() {}

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();

            if (player.getHealth() - event.getFinalDamage() <= 0.0D) {
                event.setCancelled(true);
                this.runDeath(player);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Options.YOptions yOptions = this.options.getYOptions();

        if (yOptions != null) {
            final Location to = event.getTo();

            if (to.getY() <= yOptions.getMinimumY()) {
                this.runDeath(event.getPlayer());
            }
        }
    }

    /**
     * Run the death protocol on a player.<br>
     * Warning: You can call this method only if you need, else it will be done automatically
     *
     * @param player The {@link Player} used to run the protocol
     */
    public void runDeath(Player player) {
        if (!this.deadPlayers.contains(player)) {
            this.deadPlayers.add(player);

            final HyriLastHitterProtocol lastHitterProtocol = this.getProtocolManager().getProtocol(HyriLastHitterProtocol.class);
            final List<HyriLastHitterProtocol.LastHitter> lastHitters = lastHitterProtocol.getLastHitters(player);
            final HyriGamePlayer gamePlayer = this.getGamePlayer(player);

            player.spigot().setCollidesWithEntities(false);

            if (!gamePlayer.isSpectator()) {
                PlayerUtil.resetPlayer(player, true);
                PlayerUtil.addSpectatorAbilities(player);

                gamePlayer.hide(false);

                if (lastHitters != null) {
                    final Player bestLastHitter = lastHitters.get(0).getPlayer();

                    this.playDeathSound(bestLastHitter);

                    gamePlayer.setDead(this.getGamePlayer(bestLastHitter));
                } else {
                    gamePlayer.setDead(true);
                }

                // TODO Death messages

                if (this.continueDeath.test(gamePlayer)) {
                    if (this.getGame().getState() != HyriGameState.ENDED) {
                        if (this.screen != null && this.screenHandler != null) {
                            try {
                                this.screenHandler.getConstructor().newInstance().start(this.plugin, this.screen, player);
                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    if (this.getGame().getState() != HyriGameState.ENDED) {
                        player.setAllowFlight(false);
                        player.setFlying(false);

                        player.spigot().setCollidesWithEntities(true);

                        PlayerUtil.resetPotionEffects(player);

                        gamePlayer.show();
                        gamePlayer.setSpectator(true);
                    }
                }
            }

            lastHitterProtocol.removeLastHitters(player);

            this.deadPlayers.remove(player);
        }
    }

    /**
     * Play the death sound on a player
     *
     * @param player The {@link Player} to play the sound
     */
    private void playDeathSound(Player player) {
        if (this.options.isDeathSound()) {
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
        }
    }

    /**
     * Set the options of the protocol
     *
     * @param options The {@link Options} object
     * @return This {@link HyriDeathProtocol} instance
     */
    public HyriDeathProtocol withOptions(Options options) {
        this.options = options;
        return this;
    }

    /**
     * The options class of the protocol
     */
    public static class Options {

        private YOptions yOptions;
        private boolean deathSound = true;
        private boolean deathMessages = true;

        public YOptions getYOptions() {
            return this.yOptions;
        }

        public Options withYOptions(YOptions yOptions) {
            this.yOptions = yOptions;
            return this;
        }

        public boolean isDeathSound() {
            return this.deathSound;
        }

        public Options withDeathSound(boolean deathSound) {
            this.deathSound = deathSound;
            return this;
        }

        public boolean isDeathMessages() {
            return this.deathMessages;
        }

        public Options withDeathMessages(boolean deathMessages) {
            this.deathMessages = deathMessages;
            return this;
        }

        public static class YOptions {

            private double minimumY;

            public YOptions(double minimumY) {
                this.minimumY = minimumY;
            }

            public double getMinimumY() {
                return this.minimumY;
            }

            public YOptions setMinimumY(double minimumY) {
                this.minimumY = minimumY;
                return this;
            }

        }

    }

    /**
     * The screen class of the protocol
     */
    public static class Screen {

        private final int time;
        private Consumer<Player> callback;

        public Screen(int time,  Consumer<Player> callback) {
            this.time = time;
            this.callback = callback;
        }

        public int getTime() {
            return this.time;
        }

        public Consumer<Player> getCallback() {
            return this.callback;
        }

    }

    /**
     * The screen handler class for the protocol
     */
    public static abstract class ScreenHandler extends BukkitRunnable {

        /**
         * The default screen handler
         */
        public static class Default extends ScreenHandler {

            private JavaPlugin plugin;
            private Screen screen;
            private Player player;

            private int currentTime;

            @Override
            void start(JavaPlugin plugin, Screen screen, Player player) {
                this.screen = screen;
                this.player = player;
                this.plugin = plugin;
                this.currentTime = this.screen.getTime();

                this.runTaskTimerAsynchronously(this.plugin, 0L, 20L);
            }

            @Override
            public void run() {
                if (this.currentTime <= 0) {
                    this.cancel();

                    ThreadUtil.backOnMainThread(this.plugin, () -> this.screen.getCallback().accept(this.player));
                } else {
                    if (this.currentTime == 1) {
                        this.sendRespawnTitle(HyriCommonMessages.SECOND, 20);
                    } else {
                        this.sendRespawnTitle(HyriCommonMessages.SECONDS, 30);
                    }
                }
                this.currentTime--;
            }


            private void sendRespawnTitle(HyriLanguageMessage message, int stayTime) {
                final String title = ChatColor.DARK_AQUA + Symbols.ROTATED_SQUARE + " " + DEAD.getForPlayer(this.player) + " " + Symbols.ROTATED_SQUARE;

                Title.sendTitle(this.player, title, ChatColor.AQUA + RESPAWN.getForPlayer(this.player) + ChatColor.WHITE + " " + this.currentTime + ChatColor.AQUA + " " + message.getForPlayer(this.player), 0, stayTime, 0);
            }

        }

        abstract void start(JavaPlugin plugin, Screen screen, Player player);

    }

}
