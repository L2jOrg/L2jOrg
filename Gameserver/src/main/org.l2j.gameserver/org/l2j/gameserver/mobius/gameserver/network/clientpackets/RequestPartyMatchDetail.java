package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.matching.MatchingRoom;

import java.nio.ByteBuffer;

/**
 * @author Gnacik
 */
public final class RequestPartyMatchDetail extends IClientIncomingPacket
{
    private int _roomId;
    private int _location;
    private int _level;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _roomId = packet.getInt();
        _location = packet.getInt();
        _level = packet.getInt();
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }

        if (activeChar.isInMatchingRoom())
        {
            return;
        }

        final MatchingRoom room = _roomId > 0 ? MatchingRoomManager.getInstance().getPartyMathchingRoom(_roomId) : MatchingRoomManager.getInstance().getPartyMathchingRoom(_location, _level);

        if (room != null)
        {
            room.addMember(activeChar);
        }
    }

}
