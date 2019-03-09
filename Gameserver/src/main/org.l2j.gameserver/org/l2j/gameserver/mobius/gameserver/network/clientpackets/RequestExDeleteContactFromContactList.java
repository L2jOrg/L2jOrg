package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

import java.nio.ByteBuffer;

/**
 * Format: (ch)S S: Character Name
 * @author UnAfraid & mrTJO
 */
public class RequestExDeleteContactFromContactList extends IClientIncomingPacket
{
    private String _name;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _name = readString(packet);
    }

    @Override
    public void runImpl()
    {
        if (!Config.ALLOW_MAIL)
        {
            return;
        }

        if (_name == null)
        {
            return;
        }

        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }

        activeChar.getContactList().remove(_name);
    }
}
