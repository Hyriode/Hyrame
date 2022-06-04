package fr.hyriode.hyrame.impl;

import fr.hyriode.hyrame.IHyrameConfiguration;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 14/12/2021 at 20:35
 */
public class HyrameConfiguration implements IHyrameConfiguration {

    private boolean ranksInTabList;
    private boolean buildMode;

    public HyrameConfiguration() {
        this.ranksInTabList = true;
    }

    @Override
    public boolean areRanksInTabList() {
        return this.ranksInTabList;
    }

    @Override
    public void setRanksInTabList(boolean ranksInTabList) {
        this.ranksInTabList = ranksInTabList;
    }

    @Override
    public boolean isBuildMode() {
        return this.buildMode;
    }

    @Override
    public void setBuildMode(boolean buildMode) {
        this.buildMode = buildMode;
    }

}
