package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.matching.MatchingRoom;

/**
 * @author Gnacik
 */
public final class RequestPartyMatchDetail extends ClientPacket {
    private int _roomId;
    private int _location;
    private int _level;

    @Override
    public void readImpl() {
        _roomId = readInt();
        _location = readInt();
        _level = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.isInMatchingRoom()) {
            return;
        }

        final MatchingRoom room = _roomId > 0 ? MatchingRoomManager.getInstance().getPartyMathchingRoom(_roomId) : MatchingRoomManager.getInstance().getPartyMathchingRoom(_location, _level);

        if (room != null) {
            room.addMember(activeChar);
        }
    }

}
