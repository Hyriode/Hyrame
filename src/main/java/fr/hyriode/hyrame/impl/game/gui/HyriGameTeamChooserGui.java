package fr.hyriode.hyrame.impl.game.gui;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.impl.game.util.TeamChooserItem;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import fr.hyriode.tools.inventory.AbstractInventory;
import fr.hyriode.tools.item.ItemBuilder;
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
public class HyriGameTeamChooserGui extends AbstractInventory {

    private static final HyriLanguageMessage ALREADY_IN = new HyriLanguageMessage("already.in.team")
            .addValue(HyriLanguage.FR, "Tu fais déjà partie de cette équipe !")
            .addValue(HyriLanguage.EN, "You are already in this team!");

    private static final HyriLanguageMessage FULL = new HyriLanguageMessage("team.full")
            .addValue(HyriLanguage.FR, "Cette équipe est pleine !")
            .addValue(HyriLanguage.EN, "This team is full!");

    private static final HyriLanguageMessage JOIN = new HyriLanguageMessage("team.join")
            .addValue(HyriLanguage.FR, "Tu viens de rejoindre l'équipe : ")
            .addValue(HyriLanguage.EN, "You join the team: ");

    private static final HyriLanguageMessage JOIN_RANDOM = new HyriLanguageMessage("random.join")
            .addValue(HyriLanguage.FR, "Tu seras dans une équipe aléatoire.")
            .addValue(HyriLanguage.EN, "You will be in a random team.");

    private static final HyriLanguageMessage ALREADY_IN_RANDOM = new HyriLanguageMessage("already.in.random")
            .addValue(HyriLanguage.FR, "Tu es déjà en aléatoire !")
            .addValue(HyriLanguage.EN, "You are already in random!");

    private static final HyriLanguageMessage TITLE = new HyriLanguageMessage("choose.team.gui.name")
            .addValue(HyriLanguage.FR, "Choix de l'équipe")
            .addValue(HyriLanguage.EN, "Select team");

    private static final HyriLanguageMessage RANDOM_TEAM = new HyriLanguageMessage("random.team")
            .addValue(HyriLanguage.FR, "Equipe aléatoire")
            .addValue(HyriLanguage.EN, "Random team");

    private static final String DASH = ChatColor.WHITE + " ⁃ ";

    private static final List<Player> PLAYERS = new CopyOnWriteArrayList<>();

    private final int slot;

    private final HyriGame<?> game;
    private final IHyrame hyrame;

    public HyriGameTeamChooserGui(IHyrame hyrame, HyriGame<?> game, Player owner, int slot) {
        super(owner, TITLE.getForPlayer(owner), size(game));
        this.hyrame = hyrame;
        this.game = game;
        this.slot = slot;

        this.addTeamsWools();
        this.addRandomTeamBarrier();
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

        this.setItem(this.size - 1, barrier, event -> {
            final Player player = (Player) event.getWhoClicked();
            final HyriGamePlayer gamePlayer = this.game.getPlayer(event.getWhoClicked().getUniqueId());

            if (gamePlayer.hasTeam()) {
                gamePlayer.removeFromTeam();

                this.game.updateTabList();

                if (this.slot != -1) {
                    this.hyrame.getItemManager().giveItem(this.owner, this.slot, TeamChooserItem.class);
                }

                player.sendMessage(ChatColor.DARK_AQUA + JOIN_RANDOM.getForPlayer(player));

                this.refresh();
            } else {
                player.sendMessage(ChatColor.RED + ALREADY_IN_RANDOM.getForPlayer(player));
            }
        });
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

                this.game.updateTabList();

                player.sendMessage(ChatColor.DARK_AQUA + JOIN.getForPlayer(player) + team.getDisplayName().getForPlayer(player) + ChatColor.DARK_AQUA + ".");

                if (this.slot != -1) {
                    this.hyrame.getItemManager().giveItem(this.owner, this.slot, TeamChooserItem.class);
                }

                this.refresh();
            }
        };
    }

    public void refresh() {
        for (Player player : PLAYERS) {
            new HyriGameTeamChooserGui(this.hyrame, this.game, player, this.slot).open();
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

        while (size <= game.getTeams().size() + 1) {
            size++;
        }

        while (size % 9 != 0) {
            size++;
        }

        return size;
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
