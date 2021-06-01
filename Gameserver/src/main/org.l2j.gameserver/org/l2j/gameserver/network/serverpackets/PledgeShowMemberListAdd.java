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
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class PledgeShowMemberListAdd extends ServerPacket {
    private final String _name;
    private final int _lvl;
    private final int _classId;
    private final int _isOnline;

    public PledgeShowMemberListAdd(Player player) {
        _name = player.getName();
        _lvl = player.getLevel();
        _classId = player.getClassId().getId();
        _isOnline = (player.isOnline() ? player.getObjectId() : 0);
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.PLEDGE_SHOW_MEMBER_LIST_ADD, buffer );

        buffer.writeString(_name);
        buffer.writeInt(_lvl);
        buffer.writeInt(_classId);
        buffer.writeInt(0x00);
        buffer.writeInt(0x01);
        buffer.writeInt(_isOnline); // 1 = online 0 = offline
        buffer.writeInt(0x00); // pledge type
    }

}
