package fr.hyriode.hyrame.game.gui;

import fr.hyriode.common.inventory.AbstractInventory;
import fr.hyriode.common.item.ItemBuilder;
import fr.hyriode.hyrame.Hyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.language.Language;
import fr.hyriode.hyrame.language.LanguageMessage;
import fr.hyriode.hyrame.util.References;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 17/09/2021 at 19:48
 */
public class HyriGameTeamChooseGui extends AbstractInventory {

    private static final LanguageMessage ALREADY_IN = new LanguageMessage("already.in.team")
            .addValue(Language.FR, "Tu fais déjà partie de cette équipe !")
            .addValue(Language.EN, "You are already in this team!");

    private static final LanguageMessage FULL = new LanguageMessage("team.full")
            .addValue(Language.FR, "Cette équipe est pleine !")
            .addValue(Language.EN, "This team is full!");

    private static final LanguageMessage JOIN = new LanguageMessage("team.join")
            .addValue(Language.FR, "Tu viens de rejoindre l'équipe : ")
            .addValue(Language.EN, "You join the team: ");

    private static final LanguageMessage JOIN_RANDOM = new LanguageMessage("random.join")
            .addValue(Language.FR, "Tu seras dans une équipe aléatoire.")
            .addValue(Language.EN, "You will be in a random team.");

    private static final LanguageMessage ALREADY_IN_RANDOM = new LanguageMessage("already.in.random")
            .addValue(Language.FR, "Tu es déjà en aléatoire !")
            .addValue(Language.EN, "You are already in random!");

    private static final LanguageMessage TITLE = new LanguageMessage("choose.team.gui.name")
            .addValue(Language.FR, "Choix de l'équipe")
            .addValue(Language.EN, "Select team");

    private static final LanguageMessage RANDOM_TEAM = new LanguageMessage("random.team")
            .addValue(Language.FR, "Equipe aléatoire")
            .addValue(Language.EN, "Random team");

    private static final String DASH = ChatColor.WHITE + " ⁃ ";

    private static final List<Player> PLAYERS = new CopyOnWriteArrayList<>();

    private final int slot;

    private final HyriGame<?> game;

    private final Hyrame hyrame;

    public HyriGameTeamChooseGui(Hyrame hyrame, HyriGame<?> game, Player owner, int slot) {
        super(owner, TITLE.getForPlayer(owner), size(game));
        this.hyrame = hyrame;
        this.game = game;
        this.slot = slot;

        this.addTeamsWools();
        this.addRandomTeamBarrier();
        this.addExitDoor();
    }

    public HyriGameTeamChooseGui(Hyrame hyrame, HyriGame<?> game, Player owner) {
        this(hyrame, game, owner, -1);
    }

    private void addTeamsWools() {
        for (HyriGameTeam team : this.game.getTeams()) {
            final ItemStack wool = new ItemBuilder(Material.WOOL, 1, team.getColor().getData())
                    .withName(team.getDisplayName().getForPlayer(this.owner) + ChatColor.GRAY + " [" + team.getPlayers().size() + "/" + team.getTeamSize() + "]")
                    .withLore(this.getWoolLore(team))
                    .build();

            this.addItem(wool, this.clickEvent(team));
        }
    }

    private void addRandomTeamBarrier() {
        final ItemStack barrier = new ItemBuilder(Material.BARRIER)
                .withName(ChatColor.GRAY + RANDOM_TEAM.getForPlayer(this.owner))
                .build();

        this.addItem(barrier, event -> {
            final Player player = (Player) event.getWhoClicked();
            final HyriGamePlayer gamePlayer = this.game.getPlayer(event.getWhoClicked().getUniqueId());

            if (gamePlayer.hasTeam()) {
                gamePlayer.removeFromTeam();

                this.game.updateTabList();

                if (this.slot != -1) {
                    player.getInventory().setItem(this.slot, HyriGameItems.CHOOSE_TEAM.apply(this.hyrame, gamePlayer, this.slot));
                }

                player.sendMessage(ChatColor.DARK_AQUA + JOIN_RANDOM.getForPlayer(player));

                this.refresh();
            } else {
                player.sendMessage(ChatColor.RED + ALREADY_IN_RANDOM.getForPlayer(player));
            }
        });
    }

    private void addExitDoor() {
        final ItemStack door = new ItemBuilder(Material.DARK_OAK_DOOR_ITEM)
                .withName(ChatColor.RED + References.EXIT_MESSAGE.getForPlayer(this.owner))
                .build();

        this.setItem(this.size - 5, door, event -> event.getWhoClicked().closeInventory());
    }

    private Consumer<InventoryClickEvent> clickEvent(HyriGameTeam team) {
        return event -> {
            final Player player = (Player) event.getWhoClicked();
            final HyriGamePlayer gamePlayer = this.game.getPlayer(event.getWhoClicked().getUniqueId());

            if (gamePlayer.hasTeam() && gamePlayer.isInTeam(team)) {
                player.sendMessage(ChatColor.RED + ALREADY_IN.getForPlayer(player));
            } else if (team.isFull()) {
                player.sendMessage(ChatColor.RED + FULL.getForPlayer(player));
            } else {
                gamePlayer.removeFromTeam();
                team.addPlayer(gamePlayer);
                gamePlayer.setTeam(team);

                this.game.updateTabList();

                player.sendMessage(ChatColor.DARK_AQUA + JOIN.getForPlayer(player) + team.getDisplayName().getForPlayer(player) + ChatColor.DARK_AQUA + ".");

                if (this.slot != -1) {
                    player.getInventory().setItem(this.slot, HyriGameItems.CHOOSE_TEAM.apply(this.hyrame, gamePlayer, this.slot));
                }

                this.refresh();
            }
        };
    }

    public void refresh() {
        for (Player player : PLAYERS) {
            new HyriGameTeamChooseGui(this.hyrame, this.game, player, this.slot).open();
        }
    }

    private List<String> getWoolLore(HyriGameTeam team) {
        final List<String> lore = new ArrayList<>();

        for (HyriGamePlayer player : team.getPlayers()) {
            lore.add(DASH + ChatColor.GRAY + player.getPlayer().getName());
        }

        return lore;
    }

    private static int size(HyriGame<?> game) {
        int size = 9;

        while (size <= game.getTeams().size()) {
            size++;
        }

        while (size % 9 != 0) {
            size++;
        }

        return size + 9;
    }

    @Override
    protected void onOpen(InventoryOpenEvent event) {
        PLAYERS.add((Player) event.getPlayer());
    }

    @Override
    protected void onClose(InventoryCloseEvent event) {
        PLAYERS.remove((Player) event.getPlayer());
    }

}
