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
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author -Wooden-
 */
public final class PledgeShowMemberListUpdate extends ServerPacket {
    private final String _name;
    private final int _level;
    private final int _classId;
    private final int _objectId;
    private final int _onlineStatus;
    private final int _race;
    private final int _sex;
    private final int _hasSponsor;

    public PledgeShowMemberListUpdate(Player player) {
        this(player.getClan().getClanMember(player.getObjectId()));
    }

    public PledgeShowMemberListUpdate(ClanMember member) {
        _name = member.getName();
        _level = member.getLevel();
        _classId = member.getClassId();
        _objectId = member.getObjectId();
        _race = member.getRaceOrdinal();
        _sex = member.getSex() ? 1 : 0;
        _onlineStatus = member.getOnlineStatus();
        _hasSponsor = 0;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.PLEDGE_SHOW_MEMBER_LIST_UPDATE, buffer );

        buffer.writeString(_name);
        buffer.writeInt(_level);
        buffer.writeInt(_classId);
        buffer.writeInt(_sex);
        buffer.writeInt(_race);
        if (_onlineStatus > 0) {
            buffer.writeInt(_objectId);
        } else {
            buffer.writeInt(0); // when going offline send as 0
        }
        buffer.writeInt(0x00); // pledge type
        buffer.writeInt(_hasSponsor);
        buffer.writeByte(_onlineStatus);
    }

}
