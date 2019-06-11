package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.PartyMatchingRoomLevelType;
import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Gnacik
 */
public class ListPartyWaiting extends IClientOutgoingPacket {
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
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.LIST_PARTY_WATING);

        writeInt(_size);
        writeInt(_rooms.size());
        for (MatchingRoom room : _rooms) {
            writeInt(room.getId());
            writeString(room.getTitle());
            writeInt(room.getLocation());
            writeInt(room.getMinLvl());
            writeInt(room.getMaxLvl());
            writeInt(room.getMaxMembers());
            writeString(room.getLeader().getName());
            writeInt(room.getMembersCount());
            for (L2PcInstance member : room.getMembers()) {
                writeInt(member.getClassId().getId());
                writeString(member.getName());
            }
        }
        writeInt(L2World.getInstance().getPartyCount()); // Helios
        writeInt(L2World.getInstance().getPartyMemberCount()); // Helios
    }

}
