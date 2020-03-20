package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.StartRotation;

/**
 * This class ...
 *
 * @version $Revision: 1.1.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class StartRotating extends ClientPacket {
    private int _degree;
    private int _side;

    @Override
    public void readImpl() {
        _degree = readInt();
        _side = readInt();
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
        activeChar.broadcastPacket(new StartRotation(activeChar.getObjectId(), _degree, _side, 0));
    }
}
