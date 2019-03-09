package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;

import java.nio.ByteBuffer;

/**
 * @author ShanSoft
 * structure chddSdS
 */
public final class RequestModifyBookMarkSlot extends IClientIncomingPacket
{
    private int id;
    private int icon;
    private String name;
    private String tag;

    public RequestModifyBookMarkSlot(L2GameClient client) {
        this.client = client;
    }

    @Override
    public void readImpl(ByteBuffer packet)
    {
        id = packet.getInt();
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
        activeChar.teleportBookmarkModify(id, icon, tag, name);
    }
}
