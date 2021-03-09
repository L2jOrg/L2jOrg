/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.network.serverpackets.pledge;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.data.database.data.PledgeWaitingData;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.List;

/**
 * @author Sdw
 */
public class ExPledgeDraftListSearch extends ServerPacket {
    final List<PledgeWaitingData> _pledgeRecruitList;

    public ExPledgeDraftListSearch(List<PledgeWaitingData> pledgeRecruitList) {
        _pledgeRecruitList = pledgeRecruitList;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_PLEDGE_DRAFT_LIST_SEARCH, buffer );

        buffer.writeInt(_pledgeRecruitList.size());
        for (PledgeWaitingData prl : _pledgeRecruitList) {
            buffer.writeInt(prl.getPlayerId());
            buffer.writeString(prl.getPlayerName());
            buffer.writeInt(prl.getKarma());
            buffer.writeInt(prl.getPlayerClassId());
            buffer.writeInt(prl.getPlayerLvl());
        }
    }

}
