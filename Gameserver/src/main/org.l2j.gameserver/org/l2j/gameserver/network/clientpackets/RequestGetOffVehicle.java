package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.GetOffVehicle;
import org.l2j.gameserver.network.serverpackets.StopMoveInVehicle;

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
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }
        if (!activeChar.isInBoat() || (activeChar.getBoat().getObjectId() != _boatId) || activeChar.getBoat().isMoving() || !activeChar.isInsideRadius3D(_x, _y, _z, 1000)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        activeChar.broadcastPacket(new StopMoveInVehicle(activeChar, _boatId));
        activeChar.setVehicle(null);
        activeChar.setInVehiclePosition(null);
        client.sendPacket(ActionFailed.STATIC_PACKET);
        activeChar.broadcastPacket(new GetOffVehicle(activeChar.getObjectId(), _boatId, _x, _y, _z));
        activeChar.setXYZ(_x, _y, _z);
        activeChar.setInsideZone(ZoneId.PEACE, false);
        activeChar.revalidateZone(true);
    }
}
