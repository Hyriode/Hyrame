package fr.hyriode.hyrame.impl.module.booster;

import fr.hyriode.api.booster.HyriBoosterEvent;
import fr.hyriode.api.booster.IHyriBooster;
import fr.hyriode.api.event.HyriEventHandler;

/**
 * Created by AstFaster
 * on 19/10/2022 at 14:01
 */
public class BoosterListener {

    private final BoosterModule boosterModule;

    public BoosterListener(BoosterModule boosterModule) {
        this.boosterModule = boosterModule;
    }

    @HyriEventHandler
    public void onBooster(HyriBoosterEvent event) {
        final IHyriBooster booster = event.getBooster();

        this.boosterModule.onBoosterEnabled(booster);
    }


}
