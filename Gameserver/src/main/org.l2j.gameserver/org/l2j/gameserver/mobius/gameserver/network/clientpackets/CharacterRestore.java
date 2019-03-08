package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.CharSelectInfoPackage;
import org.l2j.gameserver.mobius.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.character.player.OnPlayerRestore;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.CharSelectionInfo;

import java.nio.ByteBuffer;

/**
 * This class ...
 * @version $Revision: 1.4.2.1.2.2 $ $Date: 2005/03/27 15:29:29 $
 */
public final class CharacterRestore extends IClientIncomingPacket
{
    // cd
    private int _charSlot;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _charSlot = packet.getInt();
    }

    @Override
    public void runImpl()
    {
        if (!client.getFloodProtectors().getCharacterSelect().tryPerformAction("CharacterRestore"))
        {
            return;
        }

        client.restore(_charSlot);
        final CharSelectionInfo cl = new CharSelectionInfo(client.getAccountName(), client.getSessionId().playOkID1, 0);
        client.sendPacket(cl);
        client.setCharSelection(cl.getCharInfo());
        final CharSelectInfoPackage charInfo = client.getCharSelection(_charSlot);
        EventDispatcher.getInstance().notifyEvent(new OnPlayerRestore(charInfo.getObjectId(), charInfo.getName(), client));
    }
}
