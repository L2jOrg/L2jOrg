package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.StartRotation;

import java.nio.ByteBuffer;

/**
 * This class ...
 *
 * @version $Revision: 1.1.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class StartRotating extends IClientIncomingPacket {
    private int _degree;
    private int _side;

    @Override
    public void readImpl(ByteBuffer packet) {
        _degree = packet.getInt();
        _side = packet.getInt();
    }

    @Override
    public void runImpl() {
        if (!Config.ENABLE_KEYBOARD_MOVEMENT) {
            return;
        }

        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.isInAirShip() && activeChar.getAirShip().isCaptain(activeChar)) {
            activeChar.getAirShip().broadcastPacket(new StartRotation(activeChar.getAirShip().getObjectId(), _degree, _side, 0));
        } else {
            activeChar.broadcastPacket(new StartRotation(activeChar.getObjectId(), _degree, _side, 0));
        }
    }
}
