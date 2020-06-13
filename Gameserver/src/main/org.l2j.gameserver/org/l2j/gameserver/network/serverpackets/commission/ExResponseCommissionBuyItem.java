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
package org.l2j.gameserver.network.serverpackets.commission;

import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.commission.CommissionItem;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author NosBit
 */
public class ExResponseCommissionBuyItem extends ServerPacket {
    public static final ExResponseCommissionBuyItem FAILED = new ExResponseCommissionBuyItem(null);

    private final CommissionItem _commissionItem;

    public ExResponseCommissionBuyItem(CommissionItem commissionItem) {
        _commissionItem = commissionItem;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_RESPONSE_COMMISSION_BUY_ITEM);

        writeInt(_commissionItem != null ? 1 : 0);
        if (_commissionItem != null) {
            final ItemInfo itemInfo = _commissionItem.getItemInfo();
            writeInt(itemInfo.getEnchantLevel());
            writeInt(itemInfo.getId());
            writeLong(itemInfo.getCount());
        }
    }

}
