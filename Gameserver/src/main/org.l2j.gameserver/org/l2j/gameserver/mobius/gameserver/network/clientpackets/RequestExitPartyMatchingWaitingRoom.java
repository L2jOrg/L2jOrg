package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

import java.nio.ByteBuffer;

/**
 * @author Gnacik
 */
public final class RequestExitPartyMatchingWaitingRoom extends IClientIncomingPacket
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

        MatchingRoomManager.getInstance().removeFromWaitingList(player);
    }
}