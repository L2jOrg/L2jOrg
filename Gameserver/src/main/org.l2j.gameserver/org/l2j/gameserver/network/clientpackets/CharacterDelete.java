package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.CharacterDeleteFailType;
import org.l2j.gameserver.model.CharSelectInfoPackage;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerDelete;
import org.l2j.gameserver.network.serverpackets.CharDeleteFail;
import org.l2j.gameserver.network.serverpackets.CharDeleteSuccess;
import org.l2j.gameserver.network.serverpackets.CharSelectionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class ...
 *
 * @version $Revision: 1.8.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class CharacterDelete extends ClientPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterDelete.class);
    // cd
    private int _charSlot;

    @Override
    public void readImpl() {
        _charSlot = readInt();
    }

    @Override
    public void runImpl() {
        try {
            final CharacterDeleteFailType failType = client.markToDeleteChar(_charSlot);
            if (failType == CharacterDeleteFailType.NONE) {// Success!
                client.sendPacket(new CharDeleteSuccess());
                final CharSelectInfoPackage charInfo = client.getCharSelection(_charSlot);
                EventDispatcher.getInstance().notifyEvent(new OnPlayerDelete(charInfo.getObjectId(), charInfo.getName(), client), Listeners.players());
            } else {
                client.sendPacket(new CharDeleteFail(failType));
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }

        final CharSelectionInfo cl = new CharSelectionInfo(client.getAccountName(), client.getSessionId().getGameServerSessionId(), 0);
        client.sendPacket(cl);
        client.setCharSelection(cl.getCharInfo());
    }
}
