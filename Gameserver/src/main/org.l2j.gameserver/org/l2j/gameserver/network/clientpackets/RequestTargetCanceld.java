package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestTargetCanceld extends ClientPacket {
    private int _unselect;

    @Override
    public void readImpl() {
        _unselect = readShort();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        if (activeChar.isLockedTarget()) {
            activeChar.sendPacket(SystemMessageId.FAILED_TO_REMOVE_ENMITY);
            return;
        }

        if (_unselect == 0) {
            // Try to abort cast, if that fails, then cancel target.
            final boolean castAborted = activeChar.abortCast();
            if (!castAborted && (activeChar.getTarget() != null)) {
                activeChar.setTarget(null);
            }
        } else if (activeChar.getTarget() != null) {
            activeChar.setTarget(null);
        }
    }
}
