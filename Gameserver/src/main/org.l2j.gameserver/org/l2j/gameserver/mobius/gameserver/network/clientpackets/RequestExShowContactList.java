package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExShowContactList;

import java.nio.ByteBuffer;

/**
 * Format: (ch)
 * @author mrTJO & UnAfraid
 */
public final class RequestExShowContactList extends IClientIncomingPacket
{
    @Override
    public void readImpl(ByteBuffer packet)
    {
    }

    @Override
    public void runImpl()
    {
        if (!Config.ALLOW_MAIL)
        {
            return;
        }

        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }

        client.sendPacket(new ExShowContactList(activeChar));
    }
}
