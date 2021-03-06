/*
 * Copyright © 2019-2021 L2JOrg
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
