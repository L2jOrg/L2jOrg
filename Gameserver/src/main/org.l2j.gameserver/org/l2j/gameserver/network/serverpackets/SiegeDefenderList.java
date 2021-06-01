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
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.CASTLE_SIEGE_DEFENDER_LIST, buffer );

        buffer.writeInt(_castle.getId());
        buffer.writeInt(0x00); // Unknown
        buffer.writeInt(0x01); // Unknown
        buffer.writeInt(0x00); // Unknown

        final int size = _castle.getSiege().getDefendersWaiting().size() + _castle.getSiege().getDefenderClans().size() + (_castle.getOwner() != null ? 1 : 0);

        buffer.writeInt(size);
        buffer.writeInt(size);

        // Add owners
        final Clan ownerClan = _castle.getOwner();
        if (ownerClan != null) {
            buffer.writeInt(ownerClan.getId());
            buffer.writeString(ownerClan.getName());
            buffer.writeString(ownerClan.getLeaderName());
            buffer.writeInt(ownerClan.getCrestId());
            buffer.writeInt(0x00); // signed time (seconds) (not storated by L2J)
            buffer.writeInt(SiegeClanType.OWNER.ordinal());
            buffer.writeInt(ownerClan.getAllyId());
            buffer.writeString(ownerClan.getAllyName());
            buffer.writeString(""); // AllyLeaderName
            buffer.writeInt(ownerClan.getAllyCrestId());
        }

        // List of confirmed defenders
        for (var siegeClan : _castle.getSiege().getDefenderClans().values()) {
            final Clan defendingClan = ClanEngine.getInstance().getClan(siegeClan.getClanId());
            if ((defendingClan == null) || (defendingClan == _castle.getOwner())) {
                continue;
            }

            buffer.writeInt(defendingClan.getId());
            buffer.writeString(defendingClan.getName());
            buffer.writeString(defendingClan.getLeaderName());
            buffer.writeInt(defendingClan.getCrestId());
            buffer.writeInt(0x00); // signed time (seconds) (not storated by L2J)
            buffer.writeInt(SiegeClanType.DEFENDER.ordinal());
            buffer.writeInt(defendingClan.getAllyId());
            buffer.writeString(defendingClan.getAllyName());
            buffer.writeString(""); // AllyLeaderName
            buffer.writeInt(defendingClan.getAllyCrestId());
        }

        // List of not confirmed defenders
        for (var siegeClan : _castle.getSiege().getDefendersWaiting().values()) {
            final Clan defendingClan = ClanEngine.getInstance().getClan(siegeClan.getClanId());
            if (defendingClan == null) {
                continue;
            }

            buffer.writeInt(defendingClan.getId());
            buffer.writeString(defendingClan.getName());
            buffer.writeString(defendingClan.getLeaderName());
            buffer.writeInt(defendingClan.getCrestId());
            buffer.writeInt(0x00); // signed time (seconds) (not storated by L2J)
            buffer.writeInt(SiegeClanType.DEFENDER_PENDING.ordinal());
            buffer.writeInt(defendingClan.getAllyId());
            buffer.writeString(defendingClan.getAllyName());
            buffer.writeString(""); // AllyLeaderName
            buffer.writeInt(defendingClan.getAllyCrestId());
        }
    }

}
