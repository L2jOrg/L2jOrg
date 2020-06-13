/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.item.container;

import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;

/**
 * @author DS
 */
public class PlayerRefund extends ItemContainer {
    private final Player _owner;

    public PlayerRefund(Player owner) {
        _owner = owner;
    }

    @Override
    public String getName() {
        return "Refund";
    }

    @Override
    public Player getOwner() {
        return _owner;
    }

    @Override
    public ItemLocation getBaseLocation() {
        return ItemLocation.REFUND;
    }

    @Override
    protected void addItem(Item item) {
        super.addItem(item);
        try {
            if (getSize() > 12) {
                final Item removedItem = items.remove(0);
                if (removedItem != null) {
                    ItemEngine.getInstance().destroyItem("ClearRefund", removedItem, getOwner(), null);
                    removedItem.updateDatabase(true);
                }
            }
        } catch (Exception e) {
            LOGGER.error("addItem()", e);
        }
    }

    @Override
    public void refreshWeight() {
    }

    @Override
    public void deleteMe() {
        try {
            for (Item item : items.values()) {
                ItemEngine.getInstance().destroyItem("ClearRefund", item, getOwner(), null);
                item.updateDatabase(true);
            }
        } catch (Exception e) {
            LOGGER.error("deleteMe()", e);
        }
        items.clear();
    }

    @Override
    public void restore() {
    }
}