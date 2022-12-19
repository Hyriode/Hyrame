package fr.hyriode.hyrame.impl.host.gui.config;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.host.IHostConfig;
import fr.hyriode.hyrame.host.HostCategory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyrameMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by AstFaster
 * on 08/08/2022 at 15:20
 */
public class HostOwnConfigsGUI extends HostConfigGUI {

    public HostOwnConfigsGUI(Player owner, HostCategory parentCategory) {
        super(owner, "own-configs", parentCategory);
    }

    @Override
    protected Runnable addItems() {
        return () -> this.addConfigItems(HyriAPI.get().getHostConfigManager().getPlayerConfigs(this.owner.getUniqueId()));
    }

    @Override
    protected ItemStack createConfigItem(IHostConfig config) {
        final ItemStack original = super.createConfigItem(config);

        return original == null ? null : new ItemBuilder(original).appendLore(HyrameMessage.HOST_CLICK_TO_EDIT.asString(this.owner)).build();
    }

    @Override
    protected void onConfigRightClick(IHostConfig config) {
        new HostEditConfigGUI(this.owner, config, () -> {
            this.addItems().run();
            this.open();
        }).open();
    }

}
