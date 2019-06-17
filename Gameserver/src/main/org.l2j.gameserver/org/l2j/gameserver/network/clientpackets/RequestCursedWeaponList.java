package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.L2Character;
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
        final L2Character activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        client.sendPacket(new ExCursedWeaponList());
    }
}
