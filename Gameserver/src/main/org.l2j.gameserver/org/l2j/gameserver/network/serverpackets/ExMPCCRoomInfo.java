package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.matching.CommandChannelMatchingRoom;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author Sdw
 */
public class ExMPCCRoomInfo extends ServerPacket {
    private final CommandChannelMatchingRoom _room;

    public ExMPCCRoomInfo(CommandChannelMatchingRoom room) {
        _room = room;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_MPCC_ROOM_INFO);

        writeInt(_room.getId());
        writeInt(_room.getMaxMembers());
        writeInt(_room.getMinLvl());
        writeInt(_room.getMaxLvl());
        writeInt(_room.getLootType());
        writeInt(_room.getLocation());
        writeString(_room.getTitle());
    }

}
