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
import org.l2j.gameserver.enums.PartyMatchingRoomLevelType;
import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.world.World;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Gnacik
 */
public class ListPartyWaiting extends ServerPacket {
    private static final int NUM_PER_PAGE = 64;
    private final List<MatchingRoom> _rooms = new LinkedList<>();
    private final int _size;

    public ListPartyWaiting(PartyMatchingRoomLevelType type, int location, int page, int requestorLevel) {
        final List<MatchingRoom> rooms = MatchingRoomManager.getInstance().getPartyMathchingRooms(location, type, requestorLevel);

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
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.LIST_PARTY_WAITING, buffer );

        buffer.writeInt(_size);
        buffer.writeInt(_rooms.size());
        for (MatchingRoom room : _rooms) {
            buffer.writeInt(room.getId());
            buffer.writeString(room.getTitle());
            buffer.writeInt(room.getLocation());
            buffer.writeInt(room.getMinLvl());
            buffer.writeInt(room.getMaxLvl());
            buffer.writeInt(room.getMaxMembers());
            buffer.writeString(room.getLeader().getName());
            buffer.writeInt(room.getMembersCount());
            for (Player member : room.getMembers()) {
                buffer.writeInt(member.getClassId().getId());
                buffer.writeString(member.getName());
            }
        }
        buffer.writeInt(World.getInstance().getPartyCount()); // Helios
        buffer.writeInt(World.getInstance().getPartyMemberCount()); // Helios
    }

}
