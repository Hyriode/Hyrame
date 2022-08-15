package fr.hyriode.hyrame.impl.host.option;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.game.HyriGameType;
import fr.hyriode.api.game.IHyriGameInfo;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.game.scoreboard.HyriWaitingScoreboard;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.host.HostDisplay;
import fr.hyriode.hyrame.host.option.HostOption;
import fr.hyriode.hyrame.inventory.pagination.PaginatedInventory;
import fr.hyriode.hyrame.inventory.pagination.PaginatedItem;
import fr.hyriode.hyrame.inventory.pagination.PaginationArea;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.utils.Pagination;
import fr.hyriode.hyrame.utils.list.ListReplacer;
import fr.hyriode.hyrame.world.IWorldProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;
import java.util.List;

/**
 * Created by AstFaster
 * on 01/08/2022 at 13:22
 */
public class MapOption extends HostOption<String> {

    public MapOption(HostDisplay display) {
        super(display, HyriAPI.get().getServer().getMap());
        this.valueFormatter = player -> HyrameMessage.HOST_OPTION_MAP_FORMATTER.asString(player).replace("%map%", this.value);

        this.onChanged = map -> {
            final IHyriServer server = HyriAPI.get().getServer();
            final IWorldProvider worldProvider = HyrameLoader.getHyrame().getWorldProvider();
            final HyriWaitingRoom waitingRoom = HyrameLoader.getHyrame().getGameManager().getCurrentGame().getWaitingRoom();
            final String currentWorld = worldProvider.getCurrentWorldName();

            if (map.equals(currentWorld)) {
                return;
            }

            worldProvider.setCurrentWorld(map);
            server.setMap(map);

            if (waitingRoom != null) {
                waitingRoom.teleportPlayers();
            }

            HyriWaitingScoreboard.updateAll();
        };
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        new GUI(player).open();
    }

    private class GUI extends PaginatedInventory {

        public GUI(Player owner) {
            super(owner, ChatColor.DARK_GRAY + ChatColor.stripColor(displayName.getValue(owner)), 6 * 9);
            this.paginationManager.setArea(new PaginationArea(19, 34));

            this.setHorizontalLine(0, 8, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());
            this.setHorizontalLine(45, 53, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());

            this.setItem(49, new ItemBuilder(Material.ARROW)
                    .withName(HyrameMessage.GO_BACK.asString(this.owner))
                    .build(),
                    event -> categoryGUIProvider.apply(this.owner).open());

            this.setupItems();
        }

        private void setupItems() {
            final Pagination<PaginatedItem> pagination = this.paginationManager.getPagination();
            final IHyriServer server = HyriAPI.get().getServer();
            final String game = server.getType();
            final String gameType = server.getSubType();
            final IHyriGameInfo gameInfo = HyriAPI.get().getGameManager().getGameInfo(game);

            if (gameInfo == null) {
                return;
            }

            final HyriGameType type = gameInfo.getType(gameType);

            if (type == null) {
                return;
            }

            final List<String> maps = HyriAPI.get().getGameManager().getMaps(game, gameType);

            pagination.clear();

            for (String map : maps) {
                final ItemBuilder itemBuilder = new ItemBuilder(Material.MAP)
                        .withAllItemFlags()
                        .withName(ChatColor.AQUA + map)
                        .withLore(ListReplacer.replace(HyrameMessage.HOST_MAP_ITEM_LORE.asList(this.owner), "%game%", gameInfo.getDisplayName())
                                .replace("%game_type%", type.getDisplayName())
                                .replace("%rating%", ChatColor.RED + "TODO")
                                .list());

                if (value.equals(map)) {
                    itemBuilder.withGlow();
                }

                pagination.add(PaginatedItem.from(itemBuilder.build(), event -> {
                    setValue(map);

                    categoryGUIProvider.apply(this.owner);
                }));
            }

            this.paginationManager.updateGUI();
        }

        @Override
        public void updatePagination(int page, List<PaginatedItem> items) {
            this.addDefaultPagesItems(45, 53);
        }

    }

}
