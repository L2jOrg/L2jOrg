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
