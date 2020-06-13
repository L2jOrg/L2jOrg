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

import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Sdw
 */
public class ExListMpccWaiting extends ServerPacket {
    private static final int NUM_PER_PAGE = 64;
    private final int _size;
    private final List<MatchingRoom> _rooms = new LinkedList<>();

    public ExListMpccWaiting(int page, int location, int level) {
        final List<MatchingRoom> rooms = MatchingRoomManager.getInstance().getCCMathchingRooms(location, level);

        _size = rooms.size();
        final int startIndex = (page - 1) * NUM_PER_PAGE;
        int chunkSize = _size - startIndex;
        if (chunkSize > NUM_PER_PAGE) {
            chunkSize = NUM_PER_PAGE;
        }
        for (int i = startIndex; i < (startIndex + chunkSize); i++) {
            _rooms.add(rooms.get(i));
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_LIST_MPCC_WAITING);

        writeInt(_size);
        writeInt(_rooms.size());
        for (MatchingRoom room : _rooms) {
            writeInt(room.getId());
            writeString(room.getTitle());
            writeInt(room.getMembersCount());
            writeInt(room.getMinLvl());
            writeInt(room.getMaxLvl());
            writeInt(room.getLocation());
            writeInt(room.getMaxMembers());
            writeString(room.getLeader().getName());
        }
    }

}
