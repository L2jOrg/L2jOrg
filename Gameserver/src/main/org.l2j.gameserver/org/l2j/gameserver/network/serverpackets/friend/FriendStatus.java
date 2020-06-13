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
package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * Support for "Chat with Friends" dialog. <br />
 * Inform player about friend online status change
 *
 * @author JIV
 */
public class FriendStatus extends ServerPacket {
    public static final int OFFLINE = 0;
    public static final int ONLINE = 1;
    public static final int LEVEL = 2;
    public static final int CLASS = 3;

    private final int _type;
    private final int _objectId;
    private final int _classId;
    private final int _level;
    private final String _name;

    public FriendStatus(Player player, int type) {
        _objectId = player.getObjectId();
        _classId = player.getActiveClass();
        _level = player.getLevel();
        _name = player.getName();
        _type = type;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.FRIEND_STATUS);

        writeInt(_type);
        writeString(_name);
        switch (_type) {
            case OFFLINE -> writeInt(_objectId);
            case LEVEL -> writeInt(_level);
            case CLASS -> writeInt(_classId);
        }
    }
}
