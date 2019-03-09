package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;

import java.nio.ByteBuffer;

/**
 * @author ShanSoft
 * @structure chdSdS
 */
public final class RequestSaveBookMarkSlot extends IClientIncomingPacket
{
    private int icon;
    private String name;
    private String tag;

    public RequestSaveBookMarkSlot(L2GameClient client) {
        this.client = client;
    }

    @Override
    public void readImpl(ByteBuffer packet)
    {
        name = readString(packet);
        icon = packet.getInt();
        tag = readString(packet);
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }
        activeChar.teleportBookmarkAdd(activeChar.getX(), activeChar.getY(), activeChar.getZ(), icon, tag, name);
    }
}
