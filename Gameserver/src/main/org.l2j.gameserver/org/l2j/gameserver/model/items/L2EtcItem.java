/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.items;

import org.l2j.gameserver.model.L2ExtractableProduct;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.type.EtcItemType;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is dedicated to the management of EtcItem.
 */
public final class L2EtcItem extends L2Item {
    private String _handler;
    private EtcItemType _type;
    private List<L2ExtractableProduct> _extractableItems;
    private int _extractableCountMin;
    private int _extractableCountMax;
    private boolean _isInfinite;

    /**
     * Constructor for EtcItem.
     *
     * @param set StatsSet designating the set of couples (key,value) for description of the Etc
     */
    public L2EtcItem(StatsSet set) {
        super(set);
    }

    @Override
    public void set(StatsSet set) {
        super.set(set);
        _type = set.getEnum("etcitem_type", EtcItemType.class, EtcItemType.NONE);
        _type1 = L2Item.TYPE1_ITEM_QUESTITEM_ADENA;
        _type2 = L2Item.TYPE2_OTHER; // default is other

        if (isQuestItem()) {
            _type2 = L2Item.TYPE2_QUEST;
        } else if ((getId() == Inventory.ADENA_ID) || (getId() == Inventory.ANCIENT_ADENA_ID)) {
            _type2 = L2Item.TYPE2_MONEY;
        }

        _handler = set.getString("handler", null); // ! null !

        _extractableCountMin = set.getInt("extractableCountMin", 0);
        _extractableCountMax = set.getInt("extractableCountMax", 0);
        if (_extractableCountMin > _extractableCountMax) {
            LOGGER.warning("Item " + this + " extractableCountMin is bigger than extractableCountMax!");
        }

        _isInfinite = set.getBoolean("is_infinite", false);
    }

    /**
     * @return the type of Etc Item.
     */
    @Override
    public EtcItemType getItemType() {
        return _type;
    }

    /**
     * @return the ID of the Etc item after applying the mask.
     */
    @Override
    public int getItemMask() {
        return _type.mask();
    }

    /**
     * @return the handler name, null if no handler for item.
     */
    public String getHandlerName() {
        return _handler;
    }

    /**
     * @return the extractable items list.
     */
    public List<L2ExtractableProduct> getExtractableItems() {
        return _extractableItems;
    }

    /**
     * @return the minimum count of extractable items
     */
    public int getExtractableCountMin() {
        return _extractableCountMin;
    }

    /**
     * @return the maximum count of extractable items
     */
    public int getExtractableCountMax() {
        return _extractableCountMax;
    }

    /**
     * @return true if item is infinite
     */
    public boolean isInfinite() {
        return _isInfinite;
    }

    /**
     * @param extractableProduct
     */
    @Override
    public void addCapsuledItem(L2ExtractableProduct extractableProduct) {
        if (_extractableItems == null) {
            _extractableItems = new ArrayList<>();
        }
        _extractableItems.add(extractableProduct);
    }
}
