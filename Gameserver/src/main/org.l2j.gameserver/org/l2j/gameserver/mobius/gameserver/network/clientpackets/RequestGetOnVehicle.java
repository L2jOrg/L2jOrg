package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.instancemanager.BoatManager;
import org.l2j.gameserver.mobius.gameserver.model.Location;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2BoatInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.GetOnVehicle;

import java.nio.ByteBuffer;

/**
 * This class ...
 * @version $Revision: 1.1.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestGetOnVehicle extends IClientIncomingPacket
{
    private int _boatId;
    private Location _pos;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _boatId = packet.getInt();
        _pos = new Location(packet.getInt(), packet.getInt(), packet.getInt());
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }

        L2BoatInstance boat;
        if (activeChar.isInBoat())
        {
            boat = activeChar.getBoat();
            if (boat.getObjectId() != _boatId)
            {
                client.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
        }
        else
        {
            boat = BoatManager.getInstance().getBoat(_boatId);
            if ((boat == null) || boat.isMoving() || !activeChar.isInsideRadius3D(boat, 1000))
            {
                client.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
        }

        activeChar.setInVehiclePosition(_pos);
        activeChar.setVehicle(boat);
        activeChar.broadcastPacket(new GetOnVehicle(activeChar.getObjectId(), boat.getObjectId(), _pos));

        activeChar.setXYZ(boat.getX(), boat.getY(), boat.getZ());
        activeChar.setInsideZone(ZoneId.PEACE, true);
        activeChar.revalidateZone(true);
    }
}
