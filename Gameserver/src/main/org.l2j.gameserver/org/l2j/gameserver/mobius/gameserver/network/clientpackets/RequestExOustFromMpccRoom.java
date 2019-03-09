package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.matching.MatchingRoom;

import java.nio.ByteBuffer;

/**
 * @author jeremy
 */
public class RequestExOustFromMpccRoom extends IClientIncomingPacket
{
    private int _objectId;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _objectId = packet.getInt();
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();

        if (activeChar == null)
        {
            return;
        }

        final MatchingRoom room = activeChar.getMatchingRoom();

        if ((room != null) && (room.getLeader() == activeChar) && (room.getRoomType() == MatchingRoomType.COMMAND_CHANNEL))
        {
            final L2PcInstance player = L2World.getInstance().getPlayer(_objectId);

            if (player != null)
            {
                room.deleteMember(player, true);
            }
        }
    }
}
