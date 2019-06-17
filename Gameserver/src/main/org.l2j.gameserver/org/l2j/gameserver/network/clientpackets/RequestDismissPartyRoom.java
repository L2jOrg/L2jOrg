package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.matching.MatchingRoom;

/**
 * @author Gnacik
 */
public class RequestDismissPartyRoom extends ClientPacket {
    private int _roomid;

    @Override
    public void readImpl() {
        _roomid = readInt();
        readInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        final MatchingRoom room = player.getMatchingRoom();
        if ((room == null) || (room.getId() != _roomid) || (room.getRoomType() != MatchingRoomType.PARTY) || (room.getLeader() != player)) {
            return;
        }

        room.disbandRoom();
    }
}
