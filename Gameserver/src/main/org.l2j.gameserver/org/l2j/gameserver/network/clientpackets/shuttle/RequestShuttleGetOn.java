package org.l2j.gameserver.network.clientpackets.shuttle;

import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.Shuttle;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public class RequestShuttleGetOn extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestShuttleGetOn.class);
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
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        // TODO: better way?
        for (Shuttle shuttle : L2World.getInstance().getVisibleObjects(activeChar, Shuttle.class)) {
            if (shuttle.calculateDistance3D(activeChar) < 1000) {
                shuttle.addPassenger(activeChar);
                activeChar.getInVehiclePosition().setXYZ(_x, _y, _z);
                break;
            }
            LOGGER.info(getClass().getSimpleName() + ": range between char and shuttle: " + shuttle.calculateDistance3D(activeChar));
        }
    }
}
