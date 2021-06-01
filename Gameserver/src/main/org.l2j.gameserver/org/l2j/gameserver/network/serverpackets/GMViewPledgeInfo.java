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
import org.l2j.gameserver.data.database.data.ClanMember;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class GMViewPledgeInfo extends ServerPacket {
    private final Clan _clan;
    private final Player _activeChar;

    public GMViewPledgeInfo(Clan clan, Player activeChar) {
        _clan = clan;
        _activeChar = activeChar;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.GM_VIEW_PLEDGE_INFO, buffer );

        buffer.writeInt(0x00);
        buffer.writeString(_activeChar.getName());
        buffer.writeInt(_clan.getId());
        buffer.writeInt(0x00);
        buffer.writeString(_clan.getName());
        buffer.writeString(_clan.getLeaderName());

        buffer.writeInt(_clan.getCrestId()); // -> no, it's no longer used (nuocnam) fix by game
        buffer.writeInt(_clan.getLevel());
        buffer.writeInt(_clan.getCastleId());
        buffer.writeInt(_clan.getHideoutId());
        buffer.writeInt(0x00); // fort id
        buffer.writeInt(0x00); // rank
        buffer.writeInt(_clan.getReputationScore());
        buffer.writeInt(0x00);
        buffer.writeInt(0x00);
        buffer.writeInt(0x00);
        buffer.writeInt(_clan.getAllyId()); // c2
        buffer.writeString(_clan.getAllyName()); // c2
        buffer.writeInt(_clan.getAllyCrestId()); // c2
        buffer.writeInt(_clan.isAtWar() ? 1 : 0); // c3
        buffer.writeInt(0x00); // T3 Unknown

        buffer.writeInt(_clan.getMembers().size());
        for (ClanMember member : _clan.getMembers()) {
            if (member != null) {
                buffer.writeString(member.getName());
                buffer.writeInt(member.getLevel());
                buffer.writeInt(member.getClassId());
                buffer.writeInt(member.getSex() ? 1 : 0);
                buffer.writeInt(member.getRaceOrdinal());
                buffer.writeInt(member.isOnline() ? member.getObjectId() : 0);
                buffer.writeInt(member.getSponsor() != 0 ? 1 : 0);
            }
        }
    }


}
