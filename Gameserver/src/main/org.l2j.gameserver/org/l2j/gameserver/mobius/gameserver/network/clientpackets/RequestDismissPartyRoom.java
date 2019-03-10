package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.matching.MatchingRoom;

import java.nio.ByteBuffer;

/**
 * @author Gnacik
 */
public class RequestDismissPartyRoom extends IClientIncomingPacket {
    private int _roomid;

    @Override
    public void readImpl(ByteBuffer packet) {
        _roomid = packet.getInt();
        packet.getInt();
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
