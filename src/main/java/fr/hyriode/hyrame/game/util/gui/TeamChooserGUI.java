package fr.hyriode.hyrame.game.util.gui;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.protocol.HyriWaitingProtocol;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 17/09/2021 at 19:48
 */
public class TeamChooserGUI extends HyriInventory {

    private static final String DASH = ChatColor.WHITE + " ⁃ ";

    private static int slot;

    private final HyriGame<?> game;
    private final IHyrame hyrame;

    public TeamChooserGUI(IHyrame hyrame, HyriGame<?> game, Player owner, int slot) {
        super(owner, HyriLanguageMessage.get("team-chooser.gui.title").getValue(owner), dynamicSize(game.getTeams().size()));
        this.hyrame = hyrame;
        this.game = game;
        TeamChooserGUI.slot = slot;

        this.addTeamsWools();
        this.addRandomTeamBarrier();
    }

    @SuppressWarnings("deprecation")
    private void addTeamsWools() {
        for (HyriGameTeam team : this.game.getTeams()) {
            final ItemStack wool = new ItemBuilder(Material.WOOL, 1, team.getColor().getDyeColor().getWoolData())
                    .withName(team.getColor().getChatColor() + team.getDisplayName().getValue(this.owner) + ChatColor.GRAY + " [" + team.getPlayers().size() + "/" + team.getTeamSize() + "]")
                    .withLore(this.getWoolLore(team))
                    .build();

            this.addItem(wool, this.clickEvent(team));
        }
    }

    private void addRandomTeamBarrier() {
        final ItemStack barrier = new ItemBuilder(Material.BARRIER)
                .withName(HyriLanguageMessage.get("team-chooser.gui.random-team").getValue(this.owner))
                .build();

        this.setItem(this.size - 1, barrier, event -> {
            final Player player = (Player) event.getWhoClicked();
            final HyriGamePlayer gamePlayer = this.game.getPlayer(event.getWhoClicked().getUniqueId());

            if (gamePlayer.hasTeam()) {
                gamePlayer.removeFromTeam();

                if (this.game.isUsingGameTabList()) {
                    this.game.getTabListManager().updatePlayer(gamePlayer);
                }

                if (slot != -1) {
                    HyriGameItems.TEAM_SELECTOR.give(hyrame, gamePlayer.getPlayer(), slot);
                }

                player.sendMessage(HyriLanguageMessage.get("team-chooser.message.join-random").getValue(player));

                refresh();
            } else {
                player.sendMessage(HyriLanguageMessage.get("team-chooser.message.already-in-random").getValue(player));
            }
        });
    }

    private Consumer<InventoryClickEvent> clickEvent(HyriGameTeam team) {
        return event -> {
            final Player player = (Player) event.getWhoClicked();
            final HyriGamePlayer gamePlayer = this.game.getPlayer(event.getWhoClicked().getUniqueId());

            if (gamePlayer.hasTeam() && gamePlayer.isInTeam(team)) {
                player.sendMessage(HyriLanguageMessage.get("team-chooser.message.already-in").getValue(player));
            } else if (team.isFull()) {
                player.sendMessage(HyriLanguageMessage.get("team-chooser.message.full").getValue(player));
            } else {
                gamePlayer.removeFromTeam();

                team.addPlayer(gamePlayer);

                if (this.game.isUsingGameTabList()) {
                    this.game.getTabListManager().updatePlayer(gamePlayer);
                }

                player.sendMessage(HyriLanguageMessage.get("team-chooser.message.join").getValue(player).replace("%team%", team.getColor().getChatColor() + team.getDisplayName().getValue(player)));

                if (slot != -1) {
                    HyriGameItems.TEAM_SELECTOR.give(hyrame, gamePlayer.getPlayer(), slot);
                }

                refresh();
            }
        };
    }

    private List<String> getWoolLore(HyriGameTeam team) {
        final List<String> lore = new ArrayList<>();

        for (HyriGamePlayer player : team.getPlayers()) {
            lore.add(DASH + ChatColor.GRAY + player.getPlayer().getName());
        }

        return lore;
    }

    @Override
    public void update() {
        this.inventory.clear();

        this.addTeamsWools();
        this.addRandomTeamBarrier();
    }

    public static void refresh() {
        final IHyrame hyrame = IHyrame.get();

        for (TeamChooserGUI gui : hyrame.getInventoryManager().getInventories(TeamChooserGUI.class)) {
            gui.update();
        }

        final HyriGame<?> game = hyrame.getGameManager().getCurrentGame();

        if (!game.getProtocolManager().getProtocol(HyriWaitingProtocol.class).isTeamSelector()) {
            return;
        }

        for (HyriGamePlayer gamePlayer : game.getPlayers()) {
            if (!gamePlayer.isOnline()) {
                continue;
            }

            HyriGameItems.TEAM_SELECTOR.give(hyrame, gamePlayer.getPlayer(), slot);
        }
    }

}
