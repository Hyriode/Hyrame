package fr.hyriode.hyrame.impl.module.friend;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.util.function.BiConsumer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 10:38
 */
public class FriendCommand extends HyriCommand<HyramePlugin> {

    public static void main(String[] args) {

    }

    private static final String HYPHENS = "--------------------------------------------------";

    public FriendCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("friend")
                .withAliases("f", "ami", "friends", "amis")
                .withDescription("The command used to manage friends")
                .withType(HyriCommandType.PLAYER)
                .withUsage(getHelp(), false));
    }

    @Override
    public void handle(HyriCommandContext ctx) {

    }

    private static HyriLanguageMessage getHelp() {
        return null;
    }

}
