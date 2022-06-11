package fr.hyriode.hyrame.game.protocol;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.event.player.HyriGameDeathEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameDeathEvent.Reason;
import fr.hyriode.hyrame.game.event.player.HyriGameRespawnEvent;
import fr.hyriode.hyrame.game.util.HyriGameMessages;
import fr.hyriode.hyrame.language.HyriCommonMessages;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.PlayerUtil;
import fr.hyriode.hyrame.utils.ThreadUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    private final Map<UUID, BukkitTask> deathTasks;

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
        this.deathTasks = new HashMap<>();

        if (this.screen != null) {
            final Consumer<Player> realCallback = this.screen.getCallback();

            this.screen.callback = player -> {
                final HyriGamePlayer gamePlayer = this.getGamePlayer(player);

                if (gamePlayer == null) {
                    return;
                }

                gamePlayer.setNotDead();
                gamePlayer.show();

                player.setAllowFlight(false);
                player.setFlying(false);

                player.spigot().setCollidesWithEntities(true);

                PlayerUtil.resetPotionEffects(player);

                HyriAPI.get().getEventBus().publish(new HyriGameRespawnEvent(this.getGame(), gamePlayer));

                player.setFireTicks(0);

                realCallback.accept(player);

                this.deathTasks.remove(player.getUniqueId()).cancel();
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
    void disable() {
        for (BukkitTask task : this.deathTasks.values()) {
            task.cancel();
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            final EntityDamageEvent.DamageCause cause = event.getCause();

            if (player.getHealth() - event.getFinalDamage() <= 0.0D) {
                if (!event.isCancelled()) {
                    event.setDamage(0.0D);
                    event.setCancelled(true);

                    this.runDeath(this.getReasonFromCause(cause), player);
                }
            }
        }
    }

    private Reason getReasonFromCause(EntityDamageEvent.DamageCause cause) {
        switch (cause) {
            case FALL:
                return Reason.FALL;
            case VOID:
                return Reason.VOID;
            case BLOCK_EXPLOSION:
                return Reason.BLOCK_EXPLOSION;
            case ENTITY_EXPLOSION:
                return Reason.ENTITY_EXPLOSION;
            case LAVA:
                return Reason.LAVA;
            case FIRE:
            case FIRE_TICK:
                return Reason.FIRE;
            case LIGHTNING:
                return Reason.LIGHTNING;
            default:
                return Reason.PLAYERS;
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Options.YOptions yOptions = this.options.getYOptions();

        if (yOptions != null) {
            final Location to = event.getTo();

            if (to.getY() <= yOptions.getMinimumY()) {
                this.runDeath(Reason.VOID, event.getPlayer());
            }
        }
    }

    /**
     * Run the death protocol on a player.<br>
     * Warning: You can call this method only if you need, else it will be done automatically
     *
     * @param reason The reason to run the death
     * @param player The {@link Player} used to run the protocol
     */
    public void runDeath(Reason reason, Player player) {
        final HyriGamePlayer gamePlayer = this.getGamePlayer(player);

        if (gamePlayer == null || gamePlayer.isSpectator() || gamePlayer.isDead()) {
            return;
        }

        final HyriLastHitterProtocol lastHitterProtocol = this.getProtocolManager().getProtocol(HyriLastHitterProtocol.class);
        final List<HyriLastHitterProtocol.LastHitter> lastHitters = lastHitterProtocol.getLastHitters(player);

        player.spigot().setCollidesWithEntities(false);

        gamePlayer.hide(false);

        if (lastHitters != null) {
            final Player bestLastHitter = lastHitters.get(0).asPlayer();

            for (HyriLastHitterProtocol.LastHitter lastHitter : lastHitters) {
                if (!lastHitter.getUniqueId().equals(bestLastHitter.getUniqueId()) && lastHitter.isLast()) {
                    this.playDeathSound(lastHitter.asPlayer());
                }
            }

            this.playDeathSound(bestLastHitter);
        }

        final HyriGameDeathEvent event = gamePlayer.setDead(reason, lastHitters);

        if (this.options.isDeathMessages()) {
            this.getGame().sendMessageToAll(target -> {
                final StringBuilder builder = new StringBuilder(HyriGameMessages.createDeathMessage(gamePlayer, target, reason, lastHitters)).append(" ");

                for (HyriLanguageMessage message : event.getMessagesToAdd()) {
                    builder.append(message.getForPlayer(target)).append(" ");
                }

                return builder.toString();
            });
        }

        if (this.continueDeath.test(gamePlayer)) {
            PlayerUtil.resetPlayer(player, true);
            PlayerUtil.addSpectatorAbilities(player);

            if (this.getGame().getState() != HyriGameState.ENDED) {
                if (this.screen != null && this.screenHandler != null) {
                    try {
                        this.deathTasks.put(player.getUniqueId(), this.screenHandler.getConstructor().newInstance().start(this.plugin, this.screen, player));
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (this.getGame().getState() != HyriGameState.ENDED) {
            PlayerUtil.resetPlayer(player, true);

            player.setAllowFlight(false);
            player.setFlying(false);

            player.spigot().setCollidesWithEntities(true);

            gamePlayer.show();
            gamePlayer.setSpectator(true);
        }

        lastHitterProtocol.removeLastHitters(player);
    }

    /**
     * Play the death sound on a player
     *
     * @param player The {@link Player} to play the sound
     */
    private void playDeathSound(Player player) {
        if (player == null) {
            return;
        }

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
            BukkitTask start(JavaPlugin plugin, Screen screen, Player player) {
                this.screen = screen;
                this.player = player;
                this.plugin = plugin;
                this.currentTime = this.screen.getTime();

                return this.runTaskTimerAsynchronously(this.plugin, 0L, 20L);
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
                final String title = ChatColor.RED + DEAD.getForPlayer(this.player);

                Title.sendTitle(this.player, title, ChatColor.RED + RESPAWN.getForPlayer(this.player) + ChatColor.WHITE + " " + this.currentTime + ChatColor.RED + " " + message.getForPlayer(this.player), 0, stayTime, 0);
            }

        }

        abstract BukkitTask start(JavaPlugin plugin, Screen screen, Player player);

    }

}
