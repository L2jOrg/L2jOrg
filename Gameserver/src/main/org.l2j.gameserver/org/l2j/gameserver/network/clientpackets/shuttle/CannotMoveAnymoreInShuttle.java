package org.l2j.gameserver.network.clientpackets.shuttle;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.shuttle.ExStopMoveInShuttle;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class CannotMoveAnymoreInShuttle extends IClientIncomingPacket {
    private int _x;
    private int _y;
    private int _z;
    private int _heading;
    private int _boatId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _boatId = packet.getInt();
        _x = packet.getInt();
        _y = packet.getInt();
        _z = packet.getInt();
        _heading = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.isInShuttle()) {
            if (activeChar.getShuttle().getObjectId() == _boatId) {
                activeChar.setInVehiclePosition(new Location(_x, _y, _z));
                activeChar.setHeading(_heading);
                activeChar.broadcastPacket(new ExStopMoveInShuttle(activeChar, _boatId));
            }
        }
    }
}
