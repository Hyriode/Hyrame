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

    private final Hyrame hyrame;

    public HyrameConfiguration(Hyrame hyrame) {
        this.hyrame = hyrame;
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
    public void setRanksInTabList(boolean value) {
        if (value && !this.ranksInTabList) {
            this.hyrame.getTabManager().enableTabList();
        } else if (!value && this.ranksInTabList){
            this.hyrame.getTabManager().disableTabList();
        }

        this.ranksInTabList = value;
    }

}
