/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.actor.request;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;

/**
 * @author UnAfraid
 */
public final class EnchantItemAttributeRequest extends AbstractRequest {
    private volatile int _enchantingItemObjectId;
    private volatile int _enchantingStoneObjectId;

    public EnchantItemAttributeRequest(Player activeChar, int enchantingStoneObjectId) {
        super(activeChar);
        _enchantingStoneObjectId = enchantingStoneObjectId;
    }

    public Item getEnchantingItem() {
        return getPlayer().getInventory().getItemByObjectId(_enchantingItemObjectId);
    }

    public void setEnchantingItem(int objectId) {
        _enchantingItemObjectId = objectId;
    }

    public Item getEnchantingStone() {
        return getPlayer().getInventory().getItemByObjectId(_enchantingStoneObjectId);
    }

    public void setEnchantingStone(int objectId) {
        _enchantingStoneObjectId = objectId;
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
        return (objectId > 0) && ((objectId == _enchantingItemObjectId) || (objectId == _enchantingStoneObjectId));
    }
}
