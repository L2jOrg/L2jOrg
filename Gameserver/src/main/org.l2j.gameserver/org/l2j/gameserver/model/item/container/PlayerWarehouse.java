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

import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.actor.instance.Player;

public class PlayerWarehouse extends Warehouse {
    private final Player _owner;

    public PlayerWarehouse(Player owner) {
        _owner = owner;
    }

    @Override
    public String getName() {
        return "Warehouse";
    }

    @Override
    public Player getOwner() {
        return _owner;
    }

    @Override
    public ItemLocation getBaseLocation() {
        return ItemLocation.WAREHOUSE;
    }

    @Override
    public boolean validateCapacity(long slots) {
        return ((items.size() + slots) <= _owner.getWareHouseLimit());
    }

    @Override
    public WarehouseType getType() {
        return WarehouseType.PRIVATE;
    }
}
