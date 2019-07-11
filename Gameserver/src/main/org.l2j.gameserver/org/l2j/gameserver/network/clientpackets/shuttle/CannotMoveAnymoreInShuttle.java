package org.l2j.gameserver.network.clientpackets.shuttle;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.shuttle.ExStopMoveInShuttle;

/**
 * @author UnAfraid
 */
public class CannotMoveAnymoreInShuttle extends ClientPacket {
    private int _x;
    private int _y;
    private int _z;
    private int _heading;
    private int _boatId;

    @Override
    public void readImpl() {
        _boatId = readInt();
        _x = readInt();
        _y = readInt();
        _z = readInt();
        _heading = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
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
