package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2Party;
import org.l2j.gameserver.mobius.gameserver.model.L2Party.MessageType;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.matching.MatchingRoom;

import java.nio.ByteBuffer;

/**
 * This class ...
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestWithDrawalParty extends IClientIncomingPacket
{
    @Override
    public void readImpl(ByteBuffer packet)
    {

    }

    @Override
    public void runImpl()
    {
        final L2PcInstance player = client.getActiveChar();
        if (player == null)
        {
            return;
        }

        final L2Party party = player.getParty();
        if (party != null)
        {
            party.removePartyMember(player, MessageType.LEFT);

            final MatchingRoom room = player.getMatchingRoom();
            if (room != null)
            {
                room.deleteMember(player, false);
            }
        }
    }
}
