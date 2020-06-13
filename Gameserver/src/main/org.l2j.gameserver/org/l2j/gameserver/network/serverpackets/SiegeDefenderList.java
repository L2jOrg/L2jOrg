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

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.enums.SiegeClanType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * Populates the Siege Defender List in the SiegeInfo Window<BR>
 * <BR>
 * c = 0xcb<BR>
 * d = CastleID<BR>
 * d = unknow (0x00)<BR>
 * d = unknow (0x01)<BR>
 * d = unknow (0x00)<BR>
 * d = Number of Defending Clans?<BR>
 * d = Number of Defending Clans<BR>
 * { //repeats<BR>
 * d = ClanID<BR>
 * S = ClanName<BR>
 * S = ClanLeaderName<BR>
 * d = ClanCrestID<BR>
 * d = signed time (seconds)<BR>
 * d = Type -> Owner = 0x01 || Waiting = 0x02 || Accepted = 0x03<BR>
 * d = AllyID<BR>
 * S = AllyName<BR>
 * S = AllyLeaderName<BR>
 * d = AllyCrestID<BR>
 *
 * @author KenM
 */
public final class SiegeDefenderList extends ServerPacket {
    private final Castle _castle;

    public SiegeDefenderList(Castle castle) {
        _castle = castle;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.CASTLE_SIEGE_DEFENDER_LIST);

        writeInt(_castle.getId());
        writeInt(0x00); // Unknown
        writeInt(0x01); // Unknown
        writeInt(0x00); // Unknown

        final int size = _castle.getSiege().getDefendersWaiting().size() + _castle.getSiege().getDefenderClans().size() + (_castle.getOwner() != null ? 1 : 0);

        writeInt(size);
        writeInt(size);

        // Add owners
        final Clan ownerClan = _castle.getOwner();
        if (ownerClan != null) {
            writeInt(ownerClan.getId());
            writeString(ownerClan.getName());
            writeString(ownerClan.getLeaderName());
            writeInt(ownerClan.getCrestId());
            writeInt(0x00); // signed time (seconds) (not storated by L2J)
            writeInt(SiegeClanType.OWNER.ordinal());
            writeInt(ownerClan.getAllyId());
            writeString(ownerClan.getAllyName());
            writeString(""); // AllyLeaderName
            writeInt(ownerClan.getAllyCrestId());
        }

        // List of confirmed defenders
        for (var siegeClan : _castle.getSiege().getDefenderClans().values()) {
            final Clan defendingClan = ClanTable.getInstance().getClan(siegeClan.getClanId());
            if ((defendingClan == null) || (defendingClan == _castle.getOwner())) {
                continue;
            }

            writeInt(defendingClan.getId());
            writeString(defendingClan.getName());
            writeString(defendingClan.getLeaderName());
            writeInt(defendingClan.getCrestId());
            writeInt(0x00); // signed time (seconds) (not storated by L2J)
            writeInt(SiegeClanType.DEFENDER.ordinal());
            writeInt(defendingClan.getAllyId());
            writeString(defendingClan.getAllyName());
            writeString(""); // AllyLeaderName
            writeInt(defendingClan.getAllyCrestId());
        }

        // List of not confirmed defenders
        for (var siegeClan : _castle.getSiege().getDefendersWaiting().values()) {
            final Clan defendingClan = ClanTable.getInstance().getClan(siegeClan.getClanId());
            if (defendingClan == null) {
                continue;
            }

            writeInt(defendingClan.getId());
            writeString(defendingClan.getName());
            writeString(defendingClan.getLeaderName());
            writeInt(defendingClan.getCrestId());
            writeInt(0x00); // signed time (seconds) (not storated by L2J)
            writeInt(SiegeClanType.DEFENDER_PENDING.ordinal());
            writeInt(defendingClan.getAllyId());
            writeString(defendingClan.getAllyName());
            writeString(""); // AllyLeaderName
            writeInt(defendingClan.getAllyCrestId());
        }
    }

}
