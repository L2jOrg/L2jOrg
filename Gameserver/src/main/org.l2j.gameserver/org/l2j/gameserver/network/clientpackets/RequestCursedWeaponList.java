package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.ExCursedWeaponList;

/**
 * Format: (ch)
 *
 * @author -Wooden-
 */
public class RequestCursedWeaponList extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Creature activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        client.sendPacket(new ExCursedWeaponList());
    }
}
