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
        Tools.setup(this, this.getLogger(), () -> HyriAPI.get().getRedisResource());

        this.hyrame = new Hyrame(this);

        HyrameLoader.register(this.hyrame);
        HyrameLoader.load(new HyrameProvider(this));
    }

    @Override
    public void onDisable() {
        this.hyrame.disable();
    }

    public Hyrame getHyrame() {
        return this.hyrame;
    }

}
