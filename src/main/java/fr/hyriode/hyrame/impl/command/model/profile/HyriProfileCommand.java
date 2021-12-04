package fr.hyriode.hyrame.impl.command.model.profile;

import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyriapi.rank.HyriPermission;
import org.bukkit.command.CommandSender;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 04/12/2021 at 13:07
 */
public class HyriProfileCommand extends HyriCommand<HyramePlugin> {

    enum Permission implements HyriPermission {
        USE, INFO, EDIT
    }

    public HyriProfileCommand(HyramePlugin plugin) {
        super(plugin, "hyriprofile", "Manage actions on player profile", Permission.USE, false, false);
    }

    @Override
    public void handle(CommandSender sender, String label, String[] args) {}

}
