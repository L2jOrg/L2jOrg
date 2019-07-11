package org.l2j.gameserver.model;

import org.l2j.gameserver.model.holders.RestorationItemHolder;

import java.util.List;

/**
 * @author Zoey76
 */
public class ExtractableProductItem {
    private final List<RestorationItemHolder> _items;
    private final double _chance;

    public ExtractableProductItem(List<RestorationItemHolder> items, double chance) {
        _items = items;
        _chance = chance;
    }

    /**
     * @return the the production list.
     */
    public List<RestorationItemHolder> getItems() {
        return _items;
    }

    /**
     * @return the chance of the production list.
     */
    public double getChance() {
        return _chance;
    }
}
