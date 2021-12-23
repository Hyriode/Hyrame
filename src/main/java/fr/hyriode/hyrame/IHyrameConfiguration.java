package fr.hyriode.hyrame;

import fr.hyriode.tools.tab.Tab;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 14/12/2021 at 20:32
 */
public interface IHyrameConfiguration {

    /**
     * Get current tab lines (header and footer)
     *
     * @return Current {@link Tab}
     */
    Tab getTab();

    /**
     * Set current tab lines (header and footer)
     *
     * @param tab New {@link Tab}
     */
    void setTab(Tab tab);

    /**
     * Are ranks showed in the tab list
     *
     * @return <code>true</code> if yes
     */
    boolean areRanksInTabList();

    /**
     * Set if ranks are showed in the tab list
     *
     * @param value New value
     */
    void setRanksInTabList(boolean value);

}
