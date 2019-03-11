package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.network.serverpackets.ExCursedWeaponList;

import java.nio.ByteBuffer;

/**
 * Format: (ch)
 *
 * @author -Wooden-
 */
public class RequestCursedWeaponList extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

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
