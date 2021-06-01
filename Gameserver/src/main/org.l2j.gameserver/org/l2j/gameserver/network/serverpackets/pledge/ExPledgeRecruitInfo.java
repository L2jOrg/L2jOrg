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
import org.l2j.gameserver.engine.clan.ClanEngine;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
public class ExPledgeRecruitInfo extends ServerPacket {
    private final Clan _clan;

    public ExPledgeRecruitInfo(int clanId) {
        _clan = ClanEngine.getInstance().getClan(clanId);
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_PLEDGE_RECRUIT_INFO, buffer );

        buffer.writeString(_clan.getName());
        buffer.writeString(_clan.getLeaderName());
        buffer.writeInt(_clan.getLevel());
        buffer.writeInt(_clan.getMembersCount());
        buffer.writeInt(0x00); // sub pledges count
        // for each sub pledge write d -> id; s -> name
    }

}
