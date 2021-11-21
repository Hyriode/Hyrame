package fr.hyriode.hyrame.impl.command.model.bukkit;

import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.impl.HyramePlugin;
import org.bukkit.command.CommandSender;

import java.util.Collections;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:50
 */
public class HelpCommand extends HyriCommand<HyramePlugin> {

    public HelpCommand(HyramePlugin plugin) {
        super(plugin, "help", "Command to help you", Collections.singletonList("?"));
    }

    @Override
    public void handle(CommandSender sender, String label, String[] args) {}

}
