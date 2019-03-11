package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.matching.MatchingRoom;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class RequestExWithdrawMpccRoom extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final MatchingRoom room = activeChar.getMatchingRoom();
        if ((room != null) && (room.getRoomType() == MatchingRoomType.COMMAND_CHANNEL)) {
            room.deleteMember(activeChar, false);
        }
    }
}
