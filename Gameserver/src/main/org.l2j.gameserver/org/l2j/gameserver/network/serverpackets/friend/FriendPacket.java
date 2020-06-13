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

import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.world.World;

/**
 * Support for "Chat with Friends" dialog. <br />
 * Add new friend or delete.
 *
 * @author JIV
 */
public class FriendPacket extends ServerPacket {
    private final boolean _action;
    private final boolean _online;
    private final int _objid;
    private final String _name;

    /**
     * @param action - true for adding, false for remove
     * @param objId
     */
    public FriendPacket(boolean action, int objId) {
        _action = action;
        _objid = objId;
        _name = PlayerNameTable.getInstance().getNameById(objId);
        _online = World.getInstance().findPlayer(objId) != null;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.L2_FRIEND);

        writeInt(_action ? 1 : 3); // 1-add 3-remove
        writeInt(_objid);
        writeString(_name);
        writeInt(_online ? 1 : 0);
        writeInt(_online ? _objid : 0);
    }

}
