package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.StopRotation;

/**
 * This class ...
 *
 * @version $Revision: 1.1.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class FinishRotating extends ClientPacket {
    private int _degree;
    @SuppressWarnings("unused")
    private int _unknown;

    @Override
    public void readImpl() {
        _degree = readInt();
        _unknown = readInt();
    }

    @Override
    public void runImpl() {
        if (!Config.ENABLE_KEYBOARD_MOVEMENT) {
            return;
        }

        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        StopRotation sr = new StopRotation(activeChar.getObjectId(), _degree, 0);
        activeChar.broadcastPacket(sr);
    }
}
