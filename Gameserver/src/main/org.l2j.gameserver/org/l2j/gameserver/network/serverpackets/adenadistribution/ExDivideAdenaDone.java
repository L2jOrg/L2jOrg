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
package org.l2j.gameserver.network.serverpackets.adenadistribution;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
public class ExDivideAdenaDone extends ServerPacket {
    private final boolean _isPartyLeader;
    private final boolean _isCCLeader;
    private final long _adenaCount;
    private final long _distributedAdenaCount;
    private final int _memberCount;
    private final String _distributorName;

    public ExDivideAdenaDone(boolean isPartyLeader, boolean isCCLeader, long adenaCount, long distributedAdenaCount, int memberCount, String distributorName) {
        _isPartyLeader = isPartyLeader;
        _isCCLeader = isCCLeader;
        _adenaCount = adenaCount;
        _distributedAdenaCount = distributedAdenaCount;
        _memberCount = memberCount;
        _distributorName = distributorName;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_DIVIDE_ADENA_DONE);

        writeByte((byte) (_isPartyLeader ? 0x01 : 0x00));
        writeByte((byte) (_isCCLeader ? 0x01 : 0x00));
        writeInt(_memberCount);
        writeLong(_distributedAdenaCount);
        writeLong(_adenaCount);
        writeString(_distributorName);
    }

}
