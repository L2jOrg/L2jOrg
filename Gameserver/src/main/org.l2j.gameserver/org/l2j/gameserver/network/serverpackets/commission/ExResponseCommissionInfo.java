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

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author NosBit
 */
public class ExResponseCommissionInfo extends ServerPacket {
    public static final ExResponseCommissionInfo EMPTY = new ExResponseCommissionInfo();

    private final int _result;
    private final int _itemId;
    private final long _presetPricePerUnit;
    private final long _presetAmount;
    private final int _presetDurationType;

    private ExResponseCommissionInfo() {
        _result = 0;
        _itemId = 0;
        _presetPricePerUnit = 0;
        _presetAmount = 0;
        _presetDurationType = -1;
    }

    public ExResponseCommissionInfo(int itemId, long presetPricePerUnit, long presetAmount, int presetDurationType) {
        _result = 1;
        _itemId = itemId;
        _presetPricePerUnit = presetPricePerUnit;
        _presetAmount = presetAmount;
        _presetDurationType = presetDurationType;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_RESPONSE_COMMISSION_INFO);

        writeInt(_result);
        writeInt(_itemId);
        writeLong(_presetPricePerUnit);
        writeLong(_presetAmount);
        writeInt(_presetDurationType);
    }

}
