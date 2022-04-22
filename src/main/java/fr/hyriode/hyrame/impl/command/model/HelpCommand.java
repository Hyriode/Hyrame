package fr.hyriode.hyrame.impl.command.model;

import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.impl.HyramePlugin;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:50
 */
public class HelpCommand extends HyriCommand<HyramePlugin> {

    public HelpCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("help")
                .withAliases("?")
                .withDescription("Command used to help you"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {}

}
