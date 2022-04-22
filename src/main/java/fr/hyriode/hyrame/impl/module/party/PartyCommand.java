package fr.hyriode.hyrame.impl.module.party;

import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 10:38
 */
public class PartyCommand extends HyriCommand<HyramePlugin> {

    public PartyCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("party")
                .withAliases("p", "groupe", "group", "partie")
                .withDescription("The command used to create a party and interact with it")
                .withType(HyriCommandType.PLAYER)
                .withUsage("", false));
    }

    @Override
    public void handle(HyriCommandContext ctx) {

    }

}
