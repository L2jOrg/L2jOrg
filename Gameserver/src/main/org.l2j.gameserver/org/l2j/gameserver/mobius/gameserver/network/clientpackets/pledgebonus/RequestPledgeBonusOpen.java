package org.l2j.gameserver.mobius.gameserver.network.clientpackets.pledgebonus;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.pledgebonus.ExPledgeBonusOpen;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class RequestPledgeBonusOpen extends IClientIncomingPacket
{
    @Override
    public void readImpl(ByteBuffer packet)
    {
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance player = client.getActiveChar();
        if ((player == null) || (player.getClan() == null))
        {
            return;
        }

        player.sendPacket(new ExPledgeBonusOpen(player));
    }
}
