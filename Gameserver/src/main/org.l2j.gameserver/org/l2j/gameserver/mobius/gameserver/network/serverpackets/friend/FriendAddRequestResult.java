package org.l2j.gameserver.mobius.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class FriendAddRequestResult extends IClientOutgoingPacket {
    private final int _result;
    private final int _charId;
    private final String _charName;
    private final int _isOnline;
    private final int _charObjectId;
    private final int _charLevel;
    private final int _charClassId;

    public FriendAddRequestResult(L2PcInstance activeChar, int result) {
        _result = result;
        _charId = activeChar.getObjectId();
        _charName = activeChar.getName();
        _isOnline = activeChar.isOnlineInt();
        _charObjectId = activeChar.getObjectId();
        _charLevel = activeChar.getLevel();
        _charClassId = activeChar.getActiveClass();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.FRIEND_ADD_REQUEST_RESULT.writeId(packet);

        packet.putInt(_result);
        packet.putInt(_charId);
        writeString(_charName, packet);
        packet.putInt(_isOnline);
        packet.putInt(_charObjectId);
        packet.putInt(_charLevel);
        packet.putInt(_charClassId);
        packet.putShort((short) 0x00); // Always 0 on retail
    }
}
