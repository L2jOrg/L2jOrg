package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.BoatManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Boat;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.GetOnVehicle;

import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

/**
 * This class ...
 *
 * @version $Revision: 1.1.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestGetOnVehicle extends ClientPacket {
    private int _boatId;
    private Location _pos;

    @Override
    public void readImpl() {
        _boatId = readInt();
        _pos = new Location(readInt(), readInt(), readInt());
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        Boat boat;
        if (activeChar.isInBoat()) {
            boat = activeChar.getBoat();
            if (boat.getObjectId() != _boatId) {
                client.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
        } else {
            boat = BoatManager.getInstance().getBoat(_boatId);
            if ((boat == null) || boat.isMoving() || !isInsideRadius3D(activeChar, boat, 1000)) {
                client.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
        }

        activeChar.setInVehiclePosition(_pos);
        activeChar.setVehicle(boat);
        activeChar.broadcastPacket(new GetOnVehicle(activeChar.getObjectId(), boat.getObjectId(), _pos));

        activeChar.setXYZ(boat.getX(), boat.getY(), boat.getZ());
        activeChar.setInsideZone(ZoneType.PEACE, true);
        activeChar.revalidateZone(true);
    }
}
