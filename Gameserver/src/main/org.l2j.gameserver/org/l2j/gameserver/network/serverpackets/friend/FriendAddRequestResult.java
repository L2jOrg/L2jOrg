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
package org.l2j.gameserver.network.serverpackets.friend;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class FriendAddRequestResult extends ServerPacket {
    private final int _result;
    private final int _charId;
    private final String _charName;
    private final boolean _isOnline;
    private final int _charObjectId;
    private final int _charLevel;
    private final int _charClassId;

    public FriendAddRequestResult(Player activeChar, int result) {
        _result = result;
        _charId = activeChar.getObjectId();
        _charName = activeChar.getName();
        _isOnline = activeChar.isOnline();
        _charObjectId = activeChar.getObjectId();
        _charLevel = activeChar.getLevel();
        _charClassId = activeChar.getActiveClass();
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.FRIEND_ADD_REQUEST_RESULT, buffer );

        buffer.writeInt(_result);
        buffer.writeInt(_charId);
        buffer.writeString(_charName);
        buffer.writeInt(_isOnline);
        buffer.writeInt(_charObjectId);
        buffer.writeInt(_charLevel);
        buffer.writeInt(_charClassId);
        buffer.writeShort(0x00); // Always 0 on retail
    }

}
