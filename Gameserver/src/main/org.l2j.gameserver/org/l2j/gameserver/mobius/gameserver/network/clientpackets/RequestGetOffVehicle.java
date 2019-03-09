package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.GetOffVehicle;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.StopMoveInVehicle;

import java.nio.ByteBuffer;

/**
 * @author Maktakien
 */
public final class RequestGetOffVehicle extends IClientIncomingPacket
{
    private int _boatId;
    private int _x;
    private int _y;
    private int _z;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _boatId = packet.getInt();
        _x = packet.getInt();
        _y = packet.getInt();
        _z = packet.getInt();
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }
        if (!activeChar.isInBoat() || (activeChar.getBoat().getObjectId() != _boatId) || activeChar.getBoat().isMoving() || !activeChar.isInsideRadius3D(_x, _y, _z, 1000))
        {
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
