package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.StopMoveInVehicle;

/**
 * @author Maktakien
 */
public final class CannotMoveAnymoreInVehicle extends ClientPacket {
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
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }
        if (player.isInBoat()) {
            if (player.getBoat().getObjectId() == _boatId) {
                player.setInVehiclePosition(new Location(_x, _y, _z));
                player.setHeading(_heading);
                final StopMoveInVehicle msg = new StopMoveInVehicle(player, _boatId);
                player.broadcastPacket(msg);
            }
        }
    }
}
