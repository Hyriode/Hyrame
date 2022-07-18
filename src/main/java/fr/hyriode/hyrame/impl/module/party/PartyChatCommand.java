package fr.hyriode.hyrame.impl.module.party;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.party.IHyriParty;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 29/04/2022 at 22:15
 */
public class PartyChatCommand extends HyriCommand<HyramePlugin> {

    public PartyChatCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("partychat")
                .withAliases("pc")
                .withDescription("The command used to create a party and interact with it")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/pc <message>")
                .asynchronous());
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();
        final UUID playerId = player.getUniqueId();
        final IHyriParty party = HyriAPI.get().getPartyManager().getPlayerParty(playerId);

        if (party == null) {
            player.spigot().sendMessage(PartyModule.createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.doesnt-have").getValue(player))));
            return;
        }

        this.handleArgument(ctx, "%sentence%", output -> this.plugin.getHyrame().getPartyModule().sendPartyMessage(party, player, output.get(String.class)));
    }

}
