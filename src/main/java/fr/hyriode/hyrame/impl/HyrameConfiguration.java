package fr.hyriode.hyrame.impl;

import fr.hyriode.hyrame.IHyrameConfiguration;
import fr.hyriode.hyrame.tab.Tab;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 14/12/2021 at 20:35
 */
public class HyrameConfiguration implements IHyrameConfiguration {

    private Tab tab;
    private boolean ranksInTabList;

    public HyrameConfiguration() {
        this.ranksInTabList = true;
    }

    @Override
    public Tab getTab() {
        return this.tab;
    }

    @Override
    public void setTab(Tab tab) {
        this.tab = tab;
    }

    @Override
    public boolean areRanksInTabList() {
        return this.ranksInTabList;
    }

    @Override
    public void setRanksInTabList(boolean ranksInTabList) {
        this.ranksInTabList = ranksInTabList;
    }

}
