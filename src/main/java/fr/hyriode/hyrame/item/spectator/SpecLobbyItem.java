package fr.hyriode.hyrame.item.spectator;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.impl.HyramePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpecLobbyItem extends HyriSpectatorItem {

    private final Map<UUID, Integer> teleportTask = new HashMap<>();

    public SpecLobbyItem(HyramePlugin plugin) {
        super(plugin, "spec_lobby_item", "item.spectator.lobby.name", "item.spectator.lobby.lore", Material.BED);
    }

    @Override
    protected void onClick(IHyrame hyrame, PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();

        if (this.teleportTask.containsKey(uuid)) {
            player.sendMessage(this.lang.getValue(player, "item.spectator.lobby.cancel"));
            this.teleportTask.remove(uuid);
            return;
        }

        player.sendMessage(this.lang.getValue(player, "item.spectator.lobby.sending"));
        this.teleportTask.put(uuid, Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin,
                () -> HyriAPI.get().getServerManager().sendPlayerToLobby(player.getUniqueId()), 60));
    }
}
