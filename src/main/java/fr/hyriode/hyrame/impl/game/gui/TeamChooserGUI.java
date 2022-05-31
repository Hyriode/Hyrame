package fr.hyriode.hyrame.impl.game.gui;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.impl.game.util.TeamChooserItem;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
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
public class TeamChooserGUI extends HyriInventory {

    private static final String DASH = ChatColor.WHITE + " ⁃ ";

    private final List<Player> players = new CopyOnWriteArrayList<>();

    private final int slot;

    private final HyriGame<?> game;
    private final IHyrame hyrame;

    public TeamChooserGUI(IHyrame hyrame, HyriGame<?> game, Player owner, int slot) {
        super(owner, HyriLanguageMessage.get("team-chooser.gui.title").getForPlayer(owner), dynamicSize(game.getTeams().size()));
        this.hyrame = hyrame;
        this.game = game;
        this.slot = slot;

        this.addTeamsWools();
        this.addRandomTeamBarrier();
    }

    @SuppressWarnings("deprecation")
    private void addTeamsWools() {
        for (HyriGameTeam team : this.game.getTeams()) {
            final ItemStack wool = new ItemBuilder(Material.WOOL, 1, team.getColor().getDyeColor().getWoolData())
                    .withName(team.getColor().getChatColor() + team.getDisplayName().getForPlayer(this.owner) + ChatColor.GRAY + " [" + team.getPlayers().size() + "/" + team.getTeamSize() + "]")
                    .withLore(this.getWoolLore(team))
                    .build();

            this.addItem(wool, this.clickEvent(team));
        }
    }

    private void addRandomTeamBarrier() {
        final ItemStack barrier = new ItemBuilder(Material.BARRIER)
                .withName(HyriLanguageMessage.get("team-chooser.gui.random-team").getForPlayer(this.owner))
                .build();

        this.setItem(this.size - 1, barrier, event -> {
            final Player player = (Player) event.getWhoClicked();
            final HyriGamePlayer gamePlayer = this.game.getPlayer(event.getWhoClicked().getUniqueId());

            if (gamePlayer.hasTeam()) {
                gamePlayer.removeFromTeam();

                if (this.game.isUsingGameTabList()) {
                    this.game.getTabListManager().updatePlayer(gamePlayer);
                }

                if (this.slot != -1) {
                    this.hyrame.getItemManager().giveItem(this.owner, this.slot, TeamChooserItem.class);
                }

                player.sendMessage(HyriLanguageMessage.get("team-chooser.message.join-random").getForPlayer(player));

                this.refresh();
            } else {
                player.sendMessage(HyriLanguageMessage.get("team-chooser.message.already-in-random").getForPlayer(player));
            }
        });
    }

    private Consumer<InventoryClickEvent> clickEvent(HyriGameTeam team) {
        return event -> {
            final Player player = (Player) event.getWhoClicked();
            final HyriGamePlayer gamePlayer = this.game.getPlayer(event.getWhoClicked().getUniqueId());

            if (gamePlayer.hasTeam() && gamePlayer.isInTeam(team)) {
                player.sendMessage(HyriLanguageMessage.get("team-chooser.message.already-in").getForPlayer(player));
            } else if (team.isFull()) {
                player.sendMessage(HyriLanguageMessage.get("team-chooser.message.full").getForPlayer(player));
            } else {
                gamePlayer.removeFromTeam();

                team.addPlayer(gamePlayer);

                if (this.game.isUsingGameTabList()) {
                    this.game.getTabListManager().updatePlayer(gamePlayer);
                }

                player.sendMessage(HyriLanguageMessage.get("team-chooser.message.join").getForPlayer(player).replace("%team", team.getColor().getChatColor() + team.getDisplayName().getForPlayer(player)));

                if (this.slot != -1) {
                    this.hyrame.getItemManager().giveItem(this.owner, this.slot, TeamChooserItem.class);
                }

                this.refresh();
            }
        };
    }

    public void refresh() {
        for (Player player : this.players) {
            new TeamChooserGUI(this.hyrame, this.game, player, this.slot).open();
        }
    }

    private List<String> getWoolLore(HyriGameTeam team) {
        final List<String> lore = new ArrayList<>();

        for (HyriGamePlayer player : team.getPlayers()) {
            lore.add(DASH + ChatColor.GRAY + player.getPlayer().getName());
        }

        return lore;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        this.players.add((Player) event.getPlayer());
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        this.players.remove((Player) event.getPlayer());
    }

}
