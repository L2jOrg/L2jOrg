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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.engine.clan.ClanEngine;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * Populates the Siege Attacker List in the SiegeInfo Window<BR>
 * <BR>
 * c = ca<BR>
 * d = CastleID<BR>
 * d = unknow (0x00)<BR>
 * d = unknow (0x01)<BR>
 * d = unknow (0x00)<BR>
 * d = Number of Attackers Clans?<BR>
 * d = Number of Attackers Clans<BR>
 * { //repeats<BR>
 * d = ClanID<BR>
 * S = ClanName<BR>
 * S = ClanLeaderName<BR>
 * d = ClanCrestID<BR>
 * d = signed time (seconds)<BR>
 * d = AllyID<BR>
 * S = AllyName<BR>
 * S = AllyLeaderName<BR>
 * d = AllyCrestID<BR>
 *
 * @author KenM
 */
public final class SiegeAttackerList extends ServerPacket {
    private final Castle _castle;

    public SiegeAttackerList(Castle castle) {
        _castle = castle;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.CASTLE_SIEGE_ATTACKER_LIST, buffer );

        buffer.writeInt(_castle.getId());
        buffer.writeInt(0x00); // 0
        buffer.writeInt(0x01); // 1
        buffer.writeInt(0x00); // 0
        final int size = _castle.getSiege().getAttackerClans().size();
        if (size > 0) {
            Clan clan;

            buffer.writeInt(size);
            buffer.writeInt(size);
            for (var siegeclan : _castle.getSiege().getAttackerClans().values()) {
                clan = ClanEngine.getInstance().getClan(siegeclan.getClanId());
                if (clan == null) {
                    continue;
                }

                buffer.writeInt(clan.getId());
                buffer.writeString(clan.getName());
                buffer.writeString(clan.getLeaderName());
                buffer.writeInt(clan.getCrestId());
                buffer.writeInt(0x00); // signed time (seconds) (not storated by L2J)
                buffer.writeInt(clan.getAllyId());
                buffer.writeString(clan.getAllyName());
                buffer.writeString(""); // AllyLeaderName
                buffer.writeInt(clan.getAllyCrestId());
            }
        } else {
            buffer.writeInt(0x00);
            buffer.writeInt(0x00);
        }
    }

}
