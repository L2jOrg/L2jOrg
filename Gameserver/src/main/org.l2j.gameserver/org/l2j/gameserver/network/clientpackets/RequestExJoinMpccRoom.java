package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.matching.MatchingRoom;

/**
 * @author Sdw
 */
public class RequestExJoinMpccRoom extends ClientPacket {
    private int _roomId;

    @Override
    public void readImpl() {
        _roomId = readInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if ((activeChar == null) || (activeChar.getMatchingRoom() != null)) {
            return;
        }

        final MatchingRoom room = MatchingRoomManager.getInstance().getCCMatchingRoom(_roomId);
        if (room != null) {
            room.addMember(activeChar);
        }
    }
}
