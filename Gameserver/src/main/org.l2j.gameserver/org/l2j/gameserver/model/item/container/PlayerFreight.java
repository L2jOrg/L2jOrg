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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class PlayerFreight extends Warehouse {
    private final Player _owner;
    private final int _ownerId;

    public PlayerFreight(int object_id) {
        _owner = null;
        _ownerId = object_id;
        restore();
    }

    public PlayerFreight(Player owner) {
        _owner = owner;
        _ownerId = owner.getObjectId();
    }

    @Override
    public int getOwnerId() {
        return _ownerId;
    }

    @Override
    public Player getOwner() {
        return _owner;
    }

    @Override
    public ItemLocation getBaseLocation() {
        return ItemLocation.FREIGHT;
    }

    @Override
    public String getName() {
        return "Freight";
    }

    @Override
    public boolean validateCapacity(long slots) {
        final int curSlots = _owner == null ? Config.ALT_FREIGHT_SLOTS : Config.ALT_FREIGHT_SLOTS;
        return ((getSize() + slots) <= curSlots);
    }

    @Override
    public void refreshWeight() {
    }

    @Override
    public WarehouseType getType() {
        return WarehouseType.PRIVATE;
    }
}