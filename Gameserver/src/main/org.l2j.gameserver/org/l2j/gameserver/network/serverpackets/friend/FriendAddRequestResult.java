package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class FriendAddRequestResult extends ServerPacket {
    private final int _result;
    private final int _charId;
    private final String _charName;
    private final int _isOnline;
    private final int _charObjectId;
    private final int _charLevel;
    private final int _charClassId;

    public FriendAddRequestResult(Player activeChar, int result) {
        _result = result;
        _charId = activeChar.getObjectId();
        _charName = activeChar.getName();
        _isOnline = activeChar.isOnlineInt();
        _charObjectId = activeChar.getObjectId();
        _charLevel = activeChar.getLevel();
        _charClassId = activeChar.getActiveClass();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.FRIEND_ADD_REQUEST_RESULT);

        writeInt(_result);
        writeInt(_charId);
        writeString(_charName);
        writeInt(_isOnline);
        writeInt(_charObjectId);
        writeInt(_charLevel);
        writeInt(_charClassId);
        writeShort((short) 0x00); // Always 0 on retail
    }

}
