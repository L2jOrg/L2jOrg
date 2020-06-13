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

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author chris_00
 */
public class ExMPCCPartyInfoUpdate extends ServerPacket {
    private final int _mode;
    private final int _LeaderOID;
    private final int _memberCount;
    private final String _name;

    /**
     * @param party
     * @param mode  0 = Remove, 1 = Add
     */
    public ExMPCCPartyInfoUpdate(Party party, int mode) {
        _name = party.getLeader().getName();
        _LeaderOID = party.getLeaderObjectId();
        _memberCount = party.getMemberCount();
        _mode = mode;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_MPCC_PARTY_INFO_UPDATE);

        writeString(_name);
        writeInt(_LeaderOID);
        writeInt(_memberCount);
        writeInt(_mode); // mode 0 = Remove Party, 1 = AddParty, maybe more...
    }

}
