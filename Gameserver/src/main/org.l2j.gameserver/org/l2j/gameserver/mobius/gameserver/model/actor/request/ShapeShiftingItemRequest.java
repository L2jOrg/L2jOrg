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
package org.l2j.gameserver.mobius.gameserver.model.actor.request;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;

/**
 * @author Sdw
 */
public class ShapeShiftingItemRequest extends AbstractRequest {
    private L2ItemInstance _appearanceStone;
    private L2ItemInstance _appearanceExtractItem;

    public ShapeShiftingItemRequest(L2PcInstance activeChar, L2ItemInstance appearanceStone) {
        super(activeChar);
        _appearanceStone = appearanceStone;
    }

    public L2ItemInstance getAppearanceStone() {
        return _appearanceStone;
    }

    public void setAppearanceStone(L2ItemInstance appearanceStone) {
        _appearanceStone = appearanceStone;
    }

    public L2ItemInstance getAppearanceExtractItem() {
        return _appearanceExtractItem;
    }

    public void setAppearanceExtractItem(L2ItemInstance appearanceExtractItem) {
        _appearanceExtractItem = appearanceExtractItem;
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
        return (objectId > 0) && ((objectId == _appearanceStone.getObjectId()) || (objectId == _appearanceExtractItem.getObjectId()));
    }
}
