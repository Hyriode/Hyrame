package fr.hyriode.hyrame.impl.host.gui;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.host.HostAdvertisementEvent;
import fr.hyriode.api.host.HostData;
import fr.hyriode.api.host.HostType;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.timer.HyriGameStartingTimer;
import fr.hyriode.hyrame.host.HostCategory;
import fr.hyriode.hyrame.host.IHostController;
import fr.hyriode.hyrame.host.gui.HostGUI;
import fr.hyriode.hyrame.impl.host.gui.config.HostOwnConfigsGUI;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.utils.HyrameHead;
import fr.hyriode.hyrame.utils.TimeUtil;
import fr.hyriode.hyrame.utils.list.ListReplacer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

/**
 * Created by AstFaster
 * on 31/07/2022 at 11:59
 */
public class HostMainGUI extends HostGUI {

    private final Advert advert;

    public HostMainGUI(Player owner, HostCategory category) {
        super(owner, name(owner, "gui.host." + category.getName() + ".name"), category);

        this.setItem(46, ItemBuilder.asHead(HyrameHead.IRON_CRATE)
                .withName(HyrameMessage.HOST_CONFIGURATIONS_NAME.asString(this.owner))
                .withLore(HyrameMessage.HOST_CONFIGURATIONS_LORE.asList(this.owner))
                .build(), event -> new HostOwnConfigsGUI(this.owner, this.category).open());

        this.advert = new Advert(this.owner);
        this.advert.addItem(this, 52);

        this.addStartItem();
    }

    private void addStartItem() {
        final HyriGame<?> game = HyrameLoader.getHyrame().getGameManager().getCurrentGame();
        final HyriGameStartingTimer timer = game.getStartingTimer();
        final boolean running = timer.isRunning();

        this.setItem(49, new ItemBuilder(Material.INK_SACK, 1, running ? 1 : 10)
                .withName(running ? HyrameMessage.HOST_CANCEL_START_GAME_NAME.asString(this.owner) : HyrameMessage.HOST_START_GAME_NAME.asString(this.owner))
                .withLore(running ? HyrameMessage.HOST_CANCEL_START_GAME_LORE.asList(this.owner) : HyrameMessage.HOST_START_GAME_LORE.asList(this.owner))
                .build(),
                event -> {
                    final HostData hostData = HyriAPI.get().getServer().getHostData();

                    if (hostData.getSecondaryHosts().contains(this.owner.getUniqueId())) {
                        this.owner.sendMessage(HyrameMessage.HOST_NOT_HOST_MESSAGE.asString(this.owner));
                        return;
                    }

                    if (game.getPlayers().size() <= 1) {
                        this.owner.sendMessage(HyrameMessage.HOST_NOT_ENOUGH_PLAYERS_MESSAGE.asString(this.owner));
                        return;
                    }

                    if (timer.isRunning()) {
                        timer.cancel(true);
                    } else {
                        timer.forceStarting();
                    }

                    this.addStartItem();
                });
    }

    @Override
    protected void addCategories() {
        super.addCategories();

        for (Map.Entry<Integer, HostCategory> entry : this.getHostController().getCategories().entrySet()) {
            final HostCategory category = entry.getValue();

            this.setItem(entry.getKey(), category.createItem(this.owner), event -> category.openGUI(this.owner));
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        this.advert.stop();
    }

    private static class Advert {

        private BukkitTask task;

        private HyriInventory currentInventory;
        private int slot;

        private final Player player;

        public Advert(Player player) {
            this.player = player;
        }

        public void addItem(HyriInventory inventory, int slot) {
            if (this.currentInventory == null) {
                this.task = Bukkit.getScheduler().runTaskTimer(HyrameLoader.getHyrame().getPlugin(), () -> this.addItem(this.currentInventory, this.slot), 0, 5L);
            }

            this.currentInventory = inventory;
            this.slot = slot;

            this.currentInventory.setItem(this.slot, new ItemBuilder(Material.BEACON)
                    .withName(HyrameMessage.HOST_ADVERT_NAME.asString(this.player))
                    .withLore(this.getHostController().getAdvertTimer() == -1 ?
                                    HyrameMessage.HOST_ADVERT_LORE.asList(this.player) :
                                    ListReplacer.replace(HyrameMessage.HOST_ADVERT_TIMER_LORE.asList(this.player), "%time%", TimeUtil.formatTime(this.getHostController().getAdvertTimer())).list())
                    .build(),
                    event -> {
                        if (this.getHostController().getAdvertTimer() != -1) {
                            return;
                        }

                        final IHyriServer server = HyriAPI.get().getServer();
                        final HostData hostData = server.getHostData();

                        if (hostData.getType() == HostType.PRIVATE) {
                            this.player.sendMessage(HyrameMessage.HOST_ADVERTISEMENT_ERROR_MESSAGE.asString(this.player));
                            return;
                        }

                        if (hostData.getSecondaryHosts().contains(this.player.getUniqueId())) {
                            this.player.sendMessage(HyrameMessage.HOST_NOT_HOST_MESSAGE.asString(this.player));
                            return;
                        }

                        HyriAPI.get().getNetworkManager().getEventBus().publish(new HostAdvertisementEvent(server.getName()));

                        this.getHostController().startAdvertTimer();

                        this.addItem(this.currentInventory, this.slot);
                    });
        }

        public void stop() {
            if (this.task == null) {
                return;
            }

            this.task.cancel();
        }

        private IHostController getHostController() {
            return HyrameLoader.getHyrame().getHostController();
        }

    }

}
