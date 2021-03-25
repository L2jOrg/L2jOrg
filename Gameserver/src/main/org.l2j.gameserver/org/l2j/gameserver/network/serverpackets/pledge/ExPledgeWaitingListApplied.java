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
import org.l2j.gameserver.data.database.data.PledgeApplicantData;
import org.l2j.gameserver.data.database.data.PledgeRecruitData;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
public class ExPledgeWaitingListApplied extends ServerPacket {
    private final PledgeApplicantData _pledgePlayerRecruitInfo;
    private final PledgeRecruitData _pledgeRecruitInfo;

    public ExPledgeWaitingListApplied(int clanId, int playerId) {
        _pledgePlayerRecruitInfo = ClanEntryManager.getInstance().getPlayerApplication(clanId, playerId);
        _pledgeRecruitInfo = ClanEntryManager.getInstance().getClanById(clanId);
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_PLEDGE_WAITING_LIST_APPLIED, buffer );

        buffer.writeInt(_pledgeRecruitInfo.getClan().getId());
        buffer.writeString(_pledgeRecruitInfo.getClan().getName());
        buffer.writeString(_pledgeRecruitInfo.getClan().getLeaderName());
        buffer.writeInt(_pledgeRecruitInfo.getClan().getLevel());
        buffer.writeInt(_pledgeRecruitInfo.getClan().getMembersCount());
        buffer.writeInt(_pledgeRecruitInfo.getKarma());
        buffer.writeString(_pledgeRecruitInfo.getInformation());
        buffer.writeString(_pledgePlayerRecruitInfo.getMessage());
    }

}
