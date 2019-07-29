package org.l2j.gameserver.network.clientpackets.shuttle;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

/**
 * @author UnAfraid
 */
public class RequestShuttleGetOff extends ClientPacket {
    private int _x;
    private int _y;
    private int _z;

    @Override
    public void readImpl() {
        readInt(); // charId
        _x = readInt();
        _y = readInt();
        _z = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        if (activeChar.getShuttle() != null) {
            activeChar.getShuttle().removePassenger(activeChar, _x, _y, _z);
        }
    }
}
