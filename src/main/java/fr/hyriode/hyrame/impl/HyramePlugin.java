package fr.hyriode.hyrame.impl;

import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.tools.Tools;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:54
 */
public class HyramePlugin extends JavaPlugin {

    private Hyrame hyrame;

    @Override
    public void onEnable() {
        this.hyrame = HyrameLoader.register(() -> new Hyrame(this));

        HyrameLoader.load(new HyrameProvider(this));
        Tools.setup(this, () -> HyriAPI.get().getRedisResource());
    }

    @Override
    public void onDisable() {
        this.hyrame.disable();
    }

    public Hyrame getHyrame() {
        return this.hyrame;
    }

}
