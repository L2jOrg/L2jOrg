package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

import java.nio.ByteBuffer;

/**
 * This class ...
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestShortCutDel extends IClientIncomingPacket
{
    private int _slot;
    private int _page;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        final int id = packet.getInt();
        _slot = id % 12;
        _page = id / 12;
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }

        if ((_page > 10) || (_page < 0))
        {
            return;
        }

        activeChar.deleteShortCut(_slot, _page);
        // client needs no confirmation. this packet is just to inform the server
    }
}
