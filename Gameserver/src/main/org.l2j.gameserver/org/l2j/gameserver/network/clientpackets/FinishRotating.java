package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.StopRotation;

import java.nio.ByteBuffer;

/**
 * This class ...
 *
 * @version $Revision: 1.1.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class FinishRotating extends IClientIncomingPacket {
    private int _degree;
    @SuppressWarnings("unused")
    private int _unknown;

    @Override
    public void readImpl(ByteBuffer packet) {
        _degree = packet.getInt();
        _unknown = packet.getInt();
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

        StopRotation sr;
        if (activeChar.isInAirShip() && activeChar.getAirShip().isCaptain(activeChar)) {
            activeChar.getAirShip().setHeading(_degree);
            sr = new StopRotation(activeChar.getAirShip().getObjectId(), _degree, 0);
            activeChar.getAirShip().broadcastPacket(sr);
        } else {
            sr = new StopRotation(activeChar.getObjectId(), _degree, 0);
            activeChar.broadcastPacket(sr);
        }
    }
}
