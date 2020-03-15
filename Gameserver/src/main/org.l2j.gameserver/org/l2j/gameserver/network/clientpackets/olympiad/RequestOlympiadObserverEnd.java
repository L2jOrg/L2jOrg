package org.l2j.gameserver.network.clientpackets.olympiad;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

/**
 * format ch c: (id) 0xD0 h: (subid) 0x12
 *
 * @author -Wooden-
 */
public final class RequestOlympiadObserverEnd extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        if (activeChar.inObserverMode()) {
            activeChar.leaveOlympiadObserverMode();
        }
    }
}