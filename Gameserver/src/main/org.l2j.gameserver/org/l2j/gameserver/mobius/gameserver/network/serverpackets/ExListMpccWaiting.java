package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.mobius.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sdw
 */
public class ExListMpccWaiting extends IClientOutgoingPacket {
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_LIST_MPCC_WAITING.writeId(packet);

        packet.putInt(_size);
        packet.putInt(_rooms.size());
        for (MatchingRoom room : _rooms) {
            packet.putInt(room.getId());
            writeString(room.getTitle(), packet);
            packet.putInt(room.getMembersCount());
            packet.putInt(room.getMinLvl());
            packet.putInt(room.getMaxLvl());
            packet.putInt(room.getLocation());
            packet.putInt(room.getMaxMembers());
            writeString(room.getLeader().getName(), packet);
        }
    }
}
