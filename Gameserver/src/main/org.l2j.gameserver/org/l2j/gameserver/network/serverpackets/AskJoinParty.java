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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.PartyDistributionType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class AskJoinParty extends ServerPacket {
    private final String _requestorName;
    private final PartyDistributionType _partyDistributionType;

    /**
     * @param requestorName
     * @param partyDistributionType
     */
    public AskJoinParty(String requestorName, PartyDistributionType partyDistributionType) {
        _requestorName = requestorName;
        _partyDistributionType = partyDistributionType;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.ASK_JOIN_PARTY);

        writeString(_requestorName);
        writeInt(_partyDistributionType.getId());
    }

}
