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
package org.l2j.gameserver.model.actor.request;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;

/**
 * @author UnAfraid
 */
public class CompoundRequest extends AbstractRequest {
    private int _itemOne;
    private int _itemTwo;

    public CompoundRequest(Player activeChar) {
        super(activeChar);
    }

    public Item getItemOne() {
        return getActiveChar().getInventory().getItemByObjectId(_itemOne);
    }

    public void setItemOne(int itemOne) {
        _itemOne = itemOne;
    }

    public Item getItemTwo() {
        return getActiveChar().getInventory().getItemByObjectId(_itemTwo);
    }

    public void setItemTwo(int itemTwo) {
        _itemTwo = itemTwo;
    }

    @Override
    public boolean isItemRequest() {
        return true;
    }

    @Override
    public boolean canWorkWith(AbstractRequest request) {
        return !request.isItemRequest();
    }

    @Override
    public boolean isUsing(int objectId) {
        return (objectId > 0) && ((objectId == _itemOne) || (objectId == _itemTwo));
    }
}
