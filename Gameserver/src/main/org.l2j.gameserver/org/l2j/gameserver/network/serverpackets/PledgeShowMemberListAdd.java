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

import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class PledgeShowMemberListAdd extends ServerPacket {
    private final String _name;
    private final int _lvl;
    private final int _classId;
    private final int _isOnline;
    private final int _pledgeType;

    public PledgeShowMemberListAdd(Player player) {
        _name = player.getName();
        _lvl = player.getLevel();
        _classId = player.getClassId().getId();
        _isOnline = (player.isOnline() ? player.getObjectId() : 0);
        _pledgeType = player.getPledgeType();
    }

    public PledgeShowMemberListAdd(ClanMember cm) {
        _name = cm.getName();
        _lvl = cm.getLevel();
        _classId = cm.getClassId();
        _isOnline = (cm.isOnline() ? cm.getObjectId() : 0);
        _pledgeType = cm.getPledgeType();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PLEDGE_SHOW_MEMBER_LIST_ADD);

        writeString(_name);
        writeInt(_lvl);
        writeInt(_classId);
        writeInt(0x00);
        writeInt(0x01);
        writeInt(_isOnline); // 1 = online 0 = offline
        writeInt(_pledgeType);
    }

}
