package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExPledgeWaitingList;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class RequestPledgeWaitingList extends IClientIncomingPacket
{
    private int _clanId;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _clanId = packet.getInt();
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if ((activeChar == null) || (activeChar.getClanId() != _clanId))
        {
            return;
        }

        client.sendPacket(new ExPledgeWaitingList(_clanId));
    }
}
