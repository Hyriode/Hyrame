package fr.hyriode.hyrame.impl.command.model.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.game.HyriGameType;
import fr.hyriode.api.game.IHyriGameInfo;
import fr.hyriode.api.game.IHyriGameManager;
import fr.hyriode.api.game.rotating.IHyriRotatingGameManager;
import fr.hyriode.api.rank.type.HyriStaffRankType;
import fr.hyriode.hyrame.anvilgui.AnvilGUI;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.inventory.pagination.PaginatedInventory;
import fr.hyriode.hyrame.inventory.pagination.PaginatedItem;
import fr.hyriode.hyrame.inventory.pagination.PaginationArea;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.item.ItemHead;
import fr.hyriode.hyrame.utils.Pagination;
import fr.hyriode.hyrame.utils.Symbols;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 01/01/2022 at 21:04
 */
public class GamesCommand extends HyriCommand<HyramePlugin> {

    public GamesCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("games")
                .withType(HyriCommandType.ALL)
                .withUsage("/games")
                .withPermission(player -> player.getRank().isSuperior(HyriAPI.get().getConfig().isDevEnvironment() ? HyriStaffRankType.DEVELOPER : HyriStaffRankType.ADMINISTRATOR))
                .withDescription("Command used to interact with games.")
                .withType(HyriCommandType.PLAYER));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        new GamesGUI(this.plugin, (Player) ctx.getSender()).open();
    }

    private static abstract class GUI extends PaginatedInventory {

        protected static final BiFunction<String, String, String> LORE_FORMATTER = (key, value) -> ChatColor.DARK_GRAY + Symbols.DOT_BOLD + ChatColor.GRAY + " " + key + ": " + ChatColor.AQUA + value;

        protected static final ItemHead GAME_HEAD = () -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ2OTQ3ZTJjZmZkMzgwNDdkZTcwM2ZkOTg3OGFjZmM5OTQyZWI3MTJlZGM4ZjI1N2IxMjljYzEwZmFkMDY2NyJ9fX0=";
        protected static final ItemHead DICE_HEAD = () -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg4MWNjMjc0N2JhNzJjYmNiMDZjM2NjMzMxNzQyY2Q5ZGUyNzFhNWJiZmZkMGVjYjE0ZjFjNmE4YjY5YmM5ZSJ9fX0=";
        protected static final ItemHead MONITOR_BACKWARD_HEAD = () -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDNjNWNlYWM0ZjViN2YzZDhlMzUxN2ViNTdkOTc3ZmM2ZGU0MTRhMmNiZTE4NDljMTYzMmRjMDhmNTJmZDgifX19";
        protected static final ItemHead MONITOR_PLUS_HEAD = () -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWM1MjM2NDUyMGIzYTliYjhlZDUxNWMwMWY4MGFiN2I5NzcwMjVjZDBiMGZmNmQ4NjQ2OGE1MTY0YzZmYjc4In19fQ==";
        protected static final ItemHead RED_BUTTON_HEAD = () -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGU1ZDcyNWRkOGJmZjI0MDczOGU4NWRiYWRhZWVjYTU0MmQ2ODAwYTc4MDIzOTM4ZjBmMjljY2JiZmNhOGQ2NiJ9fX0=";

        protected final JavaPlugin plugin;

        public GUI(JavaPlugin plugin, Player owner) {
            super(owner, ChatColor.DARK_AQUA + "Hyriode " + ChatColor.DARK_GRAY + Symbols.LINE_VERTICAL_BOLD + ChatColor.GRAY + " Jeux", 6 * 9);
            this.plugin = plugin;

            this.setHorizontalLine(0, 8, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());
            this.setHorizontalLine(45, 53, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());

            this.setItem(2, ItemBuilder.asHead(GAME_HEAD)
                    .withName(ChatColor.DARK_AQUA + "Jeux " + ChatColor.DARK_GRAY + Symbols.LINE_VERTICAL_BOLD + ChatColor.GRAY + " Menu")
                    .withLore(ChatColor.GRAY + "Affiche la liste des jeux présents", ChatColor.GRAY + "sur le serveur.", "", ChatColor.DARK_AQUA + "Cliquer pour voir")
                    .build(), event -> new GamesGUI(this.plugin, this.owner).open());

            this.setItem(6, ItemBuilder.asHead(DICE_HEAD)
                    .withName(ChatColor.DARK_AQUA + "Jeux rotatifs " + ChatColor.DARK_GRAY + Symbols.LINE_VERTICAL_BOLD + ChatColor.GRAY + " Menu")
                    .withLore(ChatColor.GRAY + "Affiche la liste des jeux rotatifs", ChatColor.GRAY + "présents sur le serveur.", "", ChatColor.DARK_AQUA + "Cliquer pour voir")
                    .build(), event -> new RotatingGamesGUI(this.plugin, this.owner).open());


            this.newUpdate(5 * 20L);
            this.paginationManager.setArea(new PaginationArea(19, 33));
            this.addPagesItems();
        }

        protected void addPagesItems() {
            if (this.paginationManager == null) {
                return;
            }

            final int totalPages = this.paginationManager.getPagination().totalPages();
            final String pagination = (this.paginationManager.currentPage() + 1) + "/" + (totalPages == 0 ? 1 : 0);

            this.setItem(45, new ItemBuilder(Material.ARROW)
                    .withName(ChatColor.DARK_AQUA + "Page précédente " + ChatColor.DARK_GRAY + Symbols.LINE_VERTICAL_BOLD + ChatColor.GRAY + " " + pagination)
                    .withLore(ChatColor.GRAY + "Passe à la page précédente.")
                    .build(), event -> this.paginationManager.previousPage());

            this.setItem(53, new ItemBuilder(Material.ARROW)
                    .withName(ChatColor.DARK_AQUA + "Page suivante " + ChatColor.DARK_GRAY + Symbols.LINE_VERTICAL_BOLD + ChatColor.GRAY + " " + pagination)
                    .withLore(ChatColor.GRAY + "Passe à la page suivante.")
                    .build(), event -> this.paginationManager.nextPage());
        }

        @Override
        public void updatePagination(int page, List<PaginatedItem> items) {
            this.addPagesItems();
        }

    }

    private static class GamesGUI extends GUI {

        public GamesGUI(JavaPlugin plugin, Player owner) {
            super(plugin, owner);

            this.setupItems();

            this.setItem(49, ItemBuilder.asHead(MONITOR_PLUS_HEAD)
                    .withName(ChatColor.AQUA + "Ajouter un jeu")
                    .withLore(ChatColor.GRAY + "Ajoute un jeu à la liste des", ChatColor.GRAY + "jeux présents sur le serveur.", "", ChatColor.DARK_AQUA + "Cliquer pour ajouter")
                    .build(),
                    event -> new AnvilGUI(this.plugin, this.owner, "Nom du jeu", null, false, player -> this.open(), null, null, (player, gameName) -> {
                        new AnvilGUI(this.plugin, this.owner, "Nom (affiché) du jeu", null, false, p -> this.open(), null, null, (p, gameDisplay) -> {
                            HyriAPI.get().getGameManager().createGameInfo(gameName, gameDisplay).update();

                            this.owner.sendMessage(ChatColor.GREEN + "Jeu correctement ajouté.");
                            this.owner.playSound(this.owner.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);

                            new GamesGUI(this.plugin, this.owner).open();

                            return null;
                        }).open();

                        return null;
                    }).open());
        }

        private void setupItems() {
            final Pagination<PaginatedItem> pagination = this.paginationManager.getPagination();

            pagination.clear();

            for (IHyriGameInfo gameInfo : HyriAPI.get().getGameManager().getGamesInfo()) {
                final ItemStack itemStack = new ItemBuilder(Material.PAPER)
                        .withName(ChatColor.AQUA + gameInfo.getDisplayName())
                        .withLore(LORE_FORMATTER.apply("Nom", gameInfo.getName()), LORE_FORMATTER.apply("Types", String.valueOf(gameInfo.getTypes().size())), "", ChatColor.DARK_AQUA + "Clic-gauche pour voir", ChatColor.RED + "Clic-droit pour supprimer")
                        .build();

                pagination.add(PaginatedItem.from(itemStack, event -> {
                    if (event.isRightClick()) {
                        HyriAPI.get().getGameManager().deleteGameInfo(gameInfo.getName());

                        this.setupItems();

                        this.owner.sendMessage(ChatColor.RED + "Le jeu a bien été supprimé.");
                        this.owner.playSound(this.owner.getLocation(), Sound.FIZZ, 0.5F, 1.0F);
                    } else if (event.isLeftClick()) {
                        new GameGUI(this.plugin, this.owner, gameInfo).open();
                    }
                }));
            }

            this.paginationManager.updateGUI();
        }

        @Override
        public void update() {
            this.setupItems();
        }

    }

    private static class GameGUI extends GUI {

        private final IHyriGameInfo gameInfo;

        public GameGUI(JavaPlugin plugin, Player owner, IHyriGameInfo gameInfo) {
            super(plugin, owner);
            this.gameInfo = gameInfo;

            this.setItem(0, ItemBuilder.asHead(MONITOR_BACKWARD_HEAD)
                    .withName(ChatColor.DARK_AQUA + "Revenir en arrière")
                    .build(), event -> new GamesGUI(this.plugin, this.owner).open());

            this.setItem(49, ItemBuilder.asHead(MONITOR_PLUS_HEAD)
                    .withName(ChatColor.AQUA + "Ajouter un type")
                    .withLore(ChatColor.GRAY + "Ajoute un type disponible pour ce", ChatColor.GRAY + "jeu.", "", ChatColor.DARK_AQUA + "Cliquer pour ajouter")
                    .build(),
                    event -> new AnvilGUI(this.plugin, this.owner, "Nom du type", null, false, player -> this.open(), null, null, (player, typeName) -> {
                        new AnvilGUI(this.plugin, this.owner, "Nom (affiché) du type", null, false, p -> this.open(), null, null, (p, typeDisplay) -> {
                            int higherId = -1;
                            for (HyriGameType gameType : this.gameInfo.getTypes().values()) {
                                if (higherId < gameType.getId()) {
                                    higherId = gameType.getId();
                                }
                            }

                            this.gameInfo.addType(typeName, new HyriGameType(higherId + 1, typeDisplay));
                            this.gameInfo.update();

                            this.owner.sendMessage(ChatColor.GREEN + "Type correctement ajouté.");
                            this.owner.playSound(this.owner.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);

                            new GameGUI(this.plugin, this.owner, this.gameInfo).open();

                            return null;
                        }).open();

                        return null;
                    }).open());

            this.setupItems();
        }

        private void setupItems() {
            final Pagination<PaginatedItem> pagination = this.paginationManager.getPagination();

            pagination.clear();

            for (Map.Entry<String, HyriGameType> entry : this.gameInfo.getTypes().entrySet().stream().sorted(Comparator.comparingInt(o -> o.getValue().getId())).collect(Collectors.toList())) {
                final String typeName = entry.getKey();
                final HyriGameType type = entry.getValue();
                final ItemStack itemStack = new ItemBuilder(Material.MAP)
                        .withName(ChatColor.AQUA + type.getDisplayName())
                        .withLore(LORE_FORMATTER.apply("Nom", typeName), LORE_FORMATTER.apply("Id", String.valueOf(type.getId())), "", ChatColor.RED + "Clic-droit pour supprimer")
                        .withAllItemFlags()
                        .build();

                pagination.add(PaginatedItem.from(itemStack, event -> {
                    if (event.isRightClick()) {
                        this.gameInfo.removeType(typeName);
                        this.gameInfo.update();

                        this.owner.sendMessage(ChatColor.RED + "Le type a bien été supprimé.");
                        this.owner.playSound(this.owner.getLocation(), Sound.FIZZ, 0.5F, 1.0F);

                        this.setupItems();
                    }
                }));
            }

            this.paginationManager.updateGUI();
        }

    }

    private static class RotatingGamesGUI extends GUI {

        public RotatingGamesGUI(JavaPlugin plugin, Player owner) {
            super(plugin, owner);

            this.setItem(49, ItemBuilder.asHead(MONITOR_PLUS_HEAD)
                            .withName(ChatColor.AQUA + "Ajouter un jeu rotatif")
                            .withLore(ChatColor.GRAY + "Ajoute un jeu rotatif à la liste", ChatColor.GRAY + "actuelle des jeux rotatifs présents", ChatColor.GRAY + "sur le serveur.", "", ChatColor.DARK_AQUA + "Cliquer pour ajouter")
                            .build(),
                    event -> new AnvilGUI(this.plugin, this.owner, "Nom du jeu à ajouter", null, false, player -> this.open(), null, null, (player, gameName) -> {
                        final IHyriGameManager gameManager = HyriAPI.get().getGameManager();
                        final IHyriRotatingGameManager rotatingGameManager = gameManager.getRotatingGameManager();
                        final IHyriGameInfo gameInfo = gameManager.getGameInfo(gameName);

                        if (gameInfo == null) {
                            this.owner.sendMessage(ChatColor.RED + "Impossible de trouver un jeu appelé: " + gameName + ".");
                            return null;
                        }

                        rotatingGameManager.addRotatingGame(rotatingGameManager.getRotatingGames().size(), gameName);

                        new RotatingGamesGUI(this.plugin, this.owner).open();

                        return null;
                    }).open());

            this.setItem(51, ItemBuilder.asHead(RED_BUTTON_HEAD)
                    .withName(ChatColor.AQUA + "Forcer la rotation")
                    .withLore(ChatColor.GRAY + "Force la rotation vers le prochain", ChatColor.GRAY + "jeu rotatif.", "", ChatColor.RED + "Clic-gauche pour forcer")
                    .build(),
                    event -> {
                        HyriAPI.get().getGameManager().getRotatingGameManager().switchToNextRotatingGame();

                        this.owner.sendMessage(ChatColor.RED + "La rotation a bien été forcée.");
                        this.owner.playSound(this.owner.getLocation(), Sound.NOTE_PLING, 1.0F, 0.5F);

                        this.setupItems();
                    });

            this.setupItems();
        }

        private void setupItems() {
            final Pagination<PaginatedItem> pagination = this.paginationManager.getPagination();
            final List<IHyriGameInfo> rotatingGames = HyriAPI.get().getGameManager().getRotatingGameManager().getRotatingGames();

            pagination.clear();

            for (int i = 0; i < rotatingGames.size(); i++) {
                final IHyriGameInfo game = rotatingGames.get(i);
                final ItemBuilder itemBuilder = new ItemBuilder(Material.PAPER)
                        .withName(ChatColor.AQUA + game.getDisplayName())
                        .withLore(LORE_FORMATTER.apply("Nom", game.getName()), LORE_FORMATTER.apply("Types", String.valueOf(game.getTypes().size())), "", LORE_FORMATTER.apply("Ordre", String.valueOf(i == 0 ? ChatColor.GREEN + "Actuel" : i)), "", ChatColor.RED + "Clic-droit pour enlever");

                if (i == 0) {
                    itemBuilder.withGlow();
                }

                pagination.add(PaginatedItem.from(itemBuilder.build(), event -> {
                    if (event.isRightClick()) {
                        HyriAPI.get().getGameManager().getRotatingGameManager().removeRotatingGame(game.getName());

                        this.setupItems();

                        this.owner.playSound(this.owner.getLocation(), Sound.FIZZ, 0.5F, 1.0F);
                    }
                }));
            }

            this.paginationManager.updateGUI();
        }

        @Override
        public void update() {
            this.setupItems();
        }

    }

}
