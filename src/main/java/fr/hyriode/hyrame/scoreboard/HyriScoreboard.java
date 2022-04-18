package fr.hyriode.hyrame.scoreboard;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.reflection.Reflection;
import fr.hyriode.hyrame.utils.PacketUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class HyriScoreboard {

    /** Scoreboard's lines */
    protected final Map<Integer, HyriScoreboardLine> lines;

    /** Check if the scoreboard is currently updating */
    private boolean updating;

    /** Is showing */
    protected boolean show;
    /** Scoreboard's display name */
    protected String displayName;
    /** Scoreboard's name */
    protected String name;
    /** Scoreboard's player */
    protected final Player player;
    /** Spigot plugin */
    protected final JavaPlugin plugin;

    /**
     * Constructor of {@link HyriScoreboard}
     *
     * @param plugin Spigot plugin
     * @param player Player
     * @param name Scoreboard's name
     * @param displayName Scoreboard's display name
     */
    public HyriScoreboard(JavaPlugin plugin, Player player, String name, String displayName) {
        this.plugin = plugin;
        this.player = player;
        this.name = name;
        this.displayName = displayName;
        this.lines = new ConcurrentHashMap<>();
        this.show = false;
    }

    /**
     * Add a blank line on the scoreboard
     *
     * @param line The number of the line
     */
    public void addBlankLine(int line) {
        String result = "";
        for (Map.Entry<Integer, HyriScoreboardLine> entry : this.lines.entrySet()) {
            if (entry.getKey() == line) {
                continue;
            }
            final HyriScoreboardLine sbLine = entry.getValue();

            if (sbLine.getValue().equals(result)) {
                result += " ";
            }
        }
        this.setLine(line, result);
    }

    /**
     * Set a line of the scoreboard
     *
     * @param line Line's number
     * @param value Line's value
     * @param scoreboardLineConsumer Consumer to call on update
     * @param ticks Ticks before updating
     */
    public void setLine(int line, String value, Consumer<HyriScoreboardLine> scoreboardLineConsumer, int ticks) {
        final HyriScoreboardLine oldLine = this.lines.get(line);

        if (oldLine != null) {
            final HyriScoreboardLine.Update oldLineUpdate = oldLine.getUpdate();

            if (oldLineUpdate != null) {
                oldLineUpdate.getRunnable().cancel();
            }
        }

        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                scoreboardLineConsumer.accept(lines.get(line));

                updateLines();
            }
        };

        this.lines.put(line, new HyriScoreboardLine(value, new HyriScoreboardLine.Update(runnable, ticks)));
    }

    /**
     * Set a line of the scoreboard
     *
     * @param line - Line's number
     * @param value - Line's value
     */
    public void setLine(int line, String value) {
        this.lines.put(line, new HyriScoreboardLine(value));
    }

    /**
     * Get formatted line if length is too big
     *
     * @param value - Line to format
     * @return - Formatted line
     */
    private String getCorrectValue(String value) {
        return value.length() > 40 ? value.substring(0, 40) : value;
    }

    /**
     * Show the scoreboard to the player
     */
    public void show() {
        if (!this.show) {
            this.create();
            this.sendLines();
            this.display();

            for (HyriScoreboardLine line : this.lines.values()) {
                final HyriScoreboardLine.Update update = line.getUpdate();

                if (update != null) {
                    update.getRunnable().runTaskTimer(this.plugin, 0, update.getTicks());
                }
            }

            this.show = true;

            HyriAPI.get().getEventBus().publish(new HyriScoreboardEvent(this, this.player, HyriScoreboardEvent.Action.SHOW));
        }
    }

    /**
     * Hide the scoreboard to the player
     */
    public void hide() {
        if (this.show) {
            for (HyriScoreboardLine line : this.lines.values()) {
                final HyriScoreboardLine.Update update = line.getUpdate();

                if (update != null) {
                    update.getRunnable().cancel();
                }
            }

            this.destroy();

            this.show = false;

            HyriAPI.get().getEventBus().publish(new HyriScoreboardEvent(this, this.player, HyriScoreboardEvent.Action.HIDE));
        }
    }

    /**
     * Create the scoreboard
     */
    private void create() {
        PacketUtil.sendPacket(this.player, this.getObjectivePacket(0, this.name));
    }

    /**
     * Display the scoreboard
     */
    private void display() {
        PacketUtil.sendPacket(this.player, this.getObjectiveDisplayPacket());
    }

    /**
     * Destroy the scoreboard
     */
    private void destroy() {
        PacketUtil.sendPacket(this.player, this.getObjectivePacket(1, this.name));
    }

    /**
     * Update scoreboard's lines
     */
    public void updateLines() {
        if (!this.updating) {
            this.updating = true;

            final String oldName = this.getOldName();

            this.create();
            this.sendLines();
            this.display();

            PacketUtil.sendPacket(this.player, this.getObjectivePacket(1, oldName));

            this.updating = false;
        }
    }

    /**
     * Send lines to the player
     */
    private void sendLines() {
        for (Map.Entry<Integer, HyriScoreboardLine> entry : this.lines.entrySet()) {
            PacketUtil.sendPacket(this.player, this.getScorePacket(this.getCorrectValue(entry.getValue().getValue()), this.lines.size() - entry.getKey() - 1, PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE));
        }
    }

    private Packet<?> getObjectivePacket(int mode, String name) {
        final PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective();

        Reflection.setField("a", packet, name);
        Reflection.setField("b", packet, this.displayName);
        Reflection.setField("c", packet, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
        Reflection.setField("d", packet, mode);

        return packet;
    }

    private Packet<?> getObjectiveDisplayPacket() {
        final PacketPlayOutScoreboardDisplayObjective packet = new PacketPlayOutScoreboardDisplayObjective();

        Reflection.setField("a", packet, 1);
        Reflection.setField("b", packet, this.name);

        return packet;
    }

    protected Packet<?> getScorePacket(String scoreName, int scoreValue, Object action) {
        final PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore();

        Reflection.setField("a", packet, scoreName);
        Reflection.setField("b", packet, this.name);
        Reflection.setField("c", packet, scoreValue);
        Reflection.setField("d", packet, action);

        return packet;
    }

    private String getOldName() {
        String old = this.name;

        if (this.name.endsWith("1")) {
            this.name = this.name.substring(0, this.name.length() - 1);
        } else {
            this.name += "1";
        }

        return old;
    }

    /**
     * Get scoreboard's display name
     *
     * @return - Scoreboard's display name
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Set scoreboard's display name
     *
     * @param displayName - New display name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;

        PacketUtil.sendPacket(this.player, this.getObjectivePacket(2, this.displayName));
    }

    /**
     * Get the player
     *
     * @return - Player object
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Check if the scoreboard is showing
     *
     * @return - <code>true</code> if yes
     */
    public boolean isShow() {
        return this.show;
    }

    /**
     * Get scoreboard's lines
     *
     * @return - A map of scoreboard's lines
     */
    public Map<Integer, HyriScoreboardLine> getLines() {
        return this.lines;
    }

}
