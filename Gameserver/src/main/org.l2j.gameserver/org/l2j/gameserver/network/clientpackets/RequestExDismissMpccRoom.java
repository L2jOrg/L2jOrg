package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.matching.MatchingRoom;

/**
 * @author Sdw
 */
public class RequestExDismissMpccRoom extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final MatchingRoom room = activeChar.getMatchingRoom();
        if ((room != null) && (room.getLeader() == activeChar) && (room.getRoomType() == MatchingRoomType.COMMAND_CHANNEL)) {
            room.disbandRoom();
        }
    }
}
