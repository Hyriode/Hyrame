package fr.hyriode.hyrame.impl.host;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.host.HostData;
import fr.hyriode.api.host.IHostConfig;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.api.world.IHyriWorld;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.HyrameLogger;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.scoreboard.HyriWaitingScoreboard;
import fr.hyriode.hyrame.host.HostCategory;
import fr.hyriode.hyrame.host.IHostController;
import fr.hyriode.hyrame.host.gui.HostGUI;
import fr.hyriode.hyrame.host.option.HostOption;
import fr.hyriode.hyrame.impl.game.gui.TeamChooserGUI;
import fr.hyriode.hyrame.impl.host.category.HostMainCategory;
import fr.hyriode.hyrame.impl.host.category.team.HostTeamsCategory;
import fr.hyriode.hyrame.impl.host.gui.HostMainGUI;
import fr.hyriode.hyrame.utils.Symbols;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

/**
 * Created by AstFaster
 * on 31/07/2022 at 11:47
 */
public class HostController implements IHostController {

    private long advertTimer = -1;

    private final List<String> maps = new ArrayList<>();

    private Map<Integer, HostCategory> categories;

    private Set<String> usedConfigs;
    private IHostConfig currentConfig;
    private boolean enabled;

    private final IHyrame hyrame;

    public HostController(IHyrame hyrame) {
        this.hyrame = hyrame;
    }

    @Override
    public void enable() {
        if (this.enabled) {
            throw new IllegalStateException("Host controller is already enabled!");
        }

        HyrameLogger.log("Enabling host controller...");

        this.enabled = true;
        this.categories = new HashMap<>();
        this.usedConfigs = new HashSet<>();

        if (!HyriAPI.get().getConfig().isDevEnvironment()) {
            HyrameLogger.log("Loading all maps for host...");

            final IHyriServer server = HyriAPI.get().getServer();
            final String type = server.getType();
            final String gameType = server.getGameType();

            for (IHyriWorld map : HyriAPI.get().getWorldManager().getWorlds(type, Objects.requireNonNull(gameType))) {
                if (!map.isEnabled()) {
                    continue;
                }

                final String mapName = map.getName();

                HyrameLogger.log("Loading '" + mapName + "' map for host...");

                map.load(new File(mapName));

                new WorldCreator(mapName).createWorld();

                this.maps.add(mapName);
            }

            this.hyrame.getWorldProvider().setCurrentWorld(server.getMap());
        }

        this.addCategory(4, new HostMainCategory());

        if (this.getGame().isUsingTeams()) {
            this.addCategory(32, new HostTeamsCategory());
        }
    }

    @Override
    public void applyConfig(IHostConfig config) {
        if (!this.enabled) {
            return;
        }

        final String configId = config.getId();

        this.resetOptions();

        if (this.usedConfigs.add(configId)) {
            HyriAPI.get().getHostConfigManager().addConfigLoading(configId);
        }

        for (HostOption<?> option : this.getOptions()) {
            final Object value = config.getValue(option.getName());

            if (value == null) {
                continue;
            }

            try {
                option.setHardValue(value);
            } catch (Exception ignored) {}
        }

        HostGUI.updateAll();
        HyriWaitingScoreboard.updateAll();
        TeamChooserGUI.refresh(this.hyrame);
    }

    @Override
    public void sendHostMessage(Player player, String message) {
        if (!this.enabled) {
            return;
        }

        final IHyriPlayer account = IHyriPlayer.get(player.getUniqueId());
        final ComponentBuilder builder = new ComponentBuilder("\n").append(Symbols.HYPHENS_LINE).color(ChatColor.DARK_AQUA).strikethrough(true)
                .append("\n").strikethrough(false)
                .append("\n")
                .append(" ").append(Symbols.DOT_BOLD).color(ChatColor.DARK_GRAY)
                .append(" Host ").color(ChatColor.AQUA)
                .append(Symbols.LINE_VERTICAL_BOLD).color(ChatColor.DARK_GRAY).append(" ")
                .append(account.getNameWithRank())
                .append(" » ").color(ChatColor.DARK_GRAY)
                .append(message).color(ChatColor.WHITE)
                .append("\n\n")
                .append(Symbols.HYPHENS_LINE).color(ChatColor.DARK_AQUA).strikethrough(true)
                .append("\n");

        for (Player target : Bukkit.getOnlinePlayers()) {
            target.spigot().sendMessage(builder.create());
            target.playSound(target.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
        }
    }

    public HostOption<?> findOption(String name) {
        for (HostOption<?> option : this.getOptions()) {
            if (option.getName().equals(name)) {
                return option;
            }
        }
        return null;
    }

    @Override
    public void resetOptions() {
        this.currentConfig = null;

        for (HostOption<?> option : this.getOptions()) {
            if (!option.isResettable()) {
                continue;
            }

            option.reset();
        }

        HostGUI.updateAll();
        HyriWaitingScoreboard.updateAll();
        TeamChooserGUI.refresh(this.hyrame);
    }

    @Override
    public List<HostOption<?>> getOptions() {
        return new ArrayList<>(this.getOptions(this.categories.values()));
    }

    private List<HostOption<?>> getOptions(Collection<HostCategory> categories) {
        final List<HostOption<?>> options = new ArrayList<>();

        for (HostCategory category : categories) {
            options.addAll(category.getOptions().values());
            options.addAll(this.getOptions(category.getSubCategories().values()));
        }
        return options;
    }

    @Override
    public void addCategory(int slot, HostCategory category) {
        if (!this.enabled) {
            return;
        }

        category.setParentGUIProvider(player -> new HostMainGUI(player, this.getCategory(HostMainCategory.NAME)));

        this.categories.put(slot, category);
    }

    @Override
    public void removeCategory(HostCategory category) {
        if (!this.enabled) {
            return;
        }

        for (Map.Entry<Integer, HostCategory> entry : this.categories.entrySet()) {
            if (entry.getValue().getName().equals(category.getName())) {
                this.categories.remove(entry.getKey());
                return;
            }
        }
    }

    @Override
    public HostCategory getCategory(String name) {
        if (!this.enabled) {
            return null;
        }

        for (HostCategory category : this.categories.values()) {
            if (category.getName().equals(name)) {
                return category;
            }
        }
        return null;
    }

    @Override
    public Map<Integer, HostCategory> getCategories() {
        return this.categories;
    }

    @Override
    public IHostConfig getCurrentConfig() {
        return this.currentConfig;
    }

    @Override
    public HostData getHostData() {
        return HyriAPI.get().getServer().getHostData();
    }

    @Override
    public void startAdvertTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (advertTimer == -1) {
                    advertTimer = 60;
                } else if (advertTimer == 0) {
                    this.cancel();

                    advertTimer = -1;
                    return;
                }

                advertTimer--;
            }
        }.runTaskTimerAsynchronously(HyrameLoader.getHyrame().getPlugin(), 0L, 20L);
    }

    @Override
    public long getAdvertTimer() {
        return this.advertTimer;
    }

    public HyriGame<?> getGame() {
        return this.hyrame.getGameManager().getCurrentGame();
    }

    public List<String> getMaps() {
        return this.maps;
    }

}
