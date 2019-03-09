package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;

import java.nio.ByteBuffer;

/**
 * @author ShanSoft Packets Structure: chdd
 */
public final class RequestTeleportBookMark extends IClientIncomingPacket
{
    private int _id;

    public RequestTeleportBookMark(L2GameClient client) {
        this.client = client;
    }

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _id = packet.getInt();
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }
        activeChar.teleportBookmarkGo(_id);
    }
}
