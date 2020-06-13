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

/**
 * Support for "Chat with Friends" dialog. <br />
 * This packet is sent only at login.
 *
 * @author mrTJO, UnAfraid
 */
public class FriendList extends AbstractFriendListPacket {

    public FriendList(Player player) {
        super(player);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.FRIEND_LIST);

        writeInt(info.size());
        for (FriendInfo info : info) {
            writeInt(info.objectId);
            writeString(info.name);
            writeInt(info.online);
            writeInt(info.online ? info.objectId : 0x00); // object id if online
            writeInt(info.classId);
            writeInt(info.level);
        }
    }
}
