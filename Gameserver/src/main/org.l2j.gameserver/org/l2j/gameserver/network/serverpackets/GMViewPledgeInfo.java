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

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanMember;
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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.GM_VIEW_PLEDGE_INFO);

        writeInt(0x00);
        writeString(_activeChar.getName());
        writeInt(_clan.getId());
        writeInt(0x00);
        writeString(_clan.getName());
        writeString(_clan.getLeaderName());

        writeInt(_clan.getCrestId()); // -> no, it's no longer used (nuocnam) fix by game
        writeInt(_clan.getLevel());
        writeInt(_clan.getCastleId());
        writeInt(_clan.getHideoutId());
        writeInt(0x00); // fort id
        writeInt(_clan.getRank());
        writeInt(_clan.getReputationScore());
        writeInt(0x00);
        writeInt(0x00);
        writeInt(0x00);
        writeInt(_clan.getAllyId()); // c2
        writeString(_clan.getAllyName()); // c2
        writeInt(_clan.getAllyCrestId()); // c2
        writeInt(_clan.isAtWar() ? 1 : 0); // c3
        writeInt(0x00); // T3 Unknown

        writeInt(_clan.getMembers().size());
        for (ClanMember member : _clan.getMembers()) {
            if (member != null) {
                writeString(member.getName());
                writeInt(member.getLevel());
                writeInt(member.getClassId());
                writeInt(member.getSex() ? 1 : 0);
                writeInt(member.getRaceOrdinal());
                writeInt(member.isOnline() ? member.getObjectId() : 0);
                writeInt(member.getSponsor() != 0 ? 1 : 0);
            }
        }
    }


}
