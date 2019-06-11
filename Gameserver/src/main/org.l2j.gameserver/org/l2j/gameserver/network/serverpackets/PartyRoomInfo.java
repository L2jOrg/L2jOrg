package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.matching.PartyMatchingRoom;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Gnacik
 */
public class PartyRoomInfo extends IClientOutgoingPacket {
    private final PartyMatchingRoom _room;

    public PartyRoomInfo(PartyMatchingRoom room) {
        _room = room;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.PARTY_ROOM_INFO);

        writeInt(_room.getId());
        writeInt(_room.getMaxMembers());
        writeInt(_room.getMinLvl());
        writeInt(_room.getMaxLvl());
        writeInt(_room.getLootType());
        writeInt(_room.getLocation());
        writeString(_room.getTitle());
    }

}
