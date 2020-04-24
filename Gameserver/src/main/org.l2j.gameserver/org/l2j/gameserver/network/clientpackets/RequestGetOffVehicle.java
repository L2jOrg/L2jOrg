package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.GetOffVehicle;
import org.l2j.gameserver.network.serverpackets.StopMoveInVehicle;
import org.l2j.gameserver.world.zone.ZoneType;

import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

/**
 * @author Maktakien
 */
public final class RequestGetOffVehicle extends ClientPacket {
    private int _boatId;
    private int _x;
    private int _y;
    private int _z;

    @Override
    public void readImpl() {
        _boatId = readInt();
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
        if (!activeChar.isInBoat() || (activeChar.getBoat().getObjectId() != _boatId) || activeChar.getBoat().isMoving() || !isInsideRadius3D(activeChar, _x, _y, _z, 1000)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        activeChar.broadcastPacket(new StopMoveInVehicle(activeChar, _boatId));
        activeChar.setVehicle(null);
        activeChar.setInVehiclePosition(null);
        client.sendPacket(ActionFailed.STATIC_PACKET);
        activeChar.broadcastPacket(new GetOffVehicle(activeChar.getObjectId(), _boatId, _x, _y, _z));
        activeChar.setXYZ(_x, _y, _z);
        activeChar.setInsideZone(ZoneType.PEACE, false);
        activeChar.revalidateZone(true);
    }
}
