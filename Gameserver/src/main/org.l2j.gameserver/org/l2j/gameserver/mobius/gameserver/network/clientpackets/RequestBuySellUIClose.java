package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

import java.nio.ByteBuffer;

public class RequestBuySellUIClose extends IClientIncomingPacket
{
    @Override
    public void readImpl(ByteBuffer packet)
    {
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if ((activeChar == null) || activeChar.isInventoryDisabled())
        {
            return;
        }

        activeChar.sendItemList();
    }
}