package org.l2j.gameserver.model.item;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.holders.ItemHolder;

/**
 * @author Zoey76
 */
public final class PcItemTemplate extends ItemHolder {
    private final boolean _equipped;

    /**
     * @param set the set containing the values for this object
     */
    public PcItemTemplate(StatsSet set) {
        super(set.getInt("id"), set.getInt("count"));
        _equipped = set.getBoolean("equipped", false);
    }

    /**
     * @return {@code true} if the items is equipped upon character creation, {@code false} otherwise
     */
    public boolean isEquipped() {
        return _equipped;
    }
}