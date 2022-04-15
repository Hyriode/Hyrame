package fr.hyriode.hyrame.impl.placeholder.handler;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.HyriConstants;
import fr.hyriode.api.network.IHyriMaintenance;
import fr.hyriode.api.network.IHyriNetwork;
import fr.hyriode.hyrame.placeholder.PlaceholderPrefixHandler;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 24/03/2022 at 09:29
 */
public class PNetworkHandler extends PlaceholderPrefixHandler {

    public PNetworkHandler() {
        super("network");
    }

    @Override
    public String handle(Player player, String placeholder) {
        final IHyriNetwork network = HyriAPI.get().getNetwork();
        final IHyriMaintenance maintenance = network.getMaintenance();

        switch (placeholder) {
            case "player_count":
                return String.valueOf(network.getPlayers());
            case "slots":
                return String.valueOf(network.getSlots());
            case "maintenance_reason":
                return maintenance.getReason();
            case "maintenance_trigger":
                if (maintenance.isActive() && maintenance.getTrigger() != null) {
                    return HyriAPI.get().getPlayerManager().getPlayer(maintenance.getTrigger()).getName();
                }
            case "ip":
                return HyriConstants.SERVER_IP;
        }
        return null;
    }

}
