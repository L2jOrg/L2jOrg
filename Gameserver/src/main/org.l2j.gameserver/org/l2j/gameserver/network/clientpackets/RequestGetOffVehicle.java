/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
