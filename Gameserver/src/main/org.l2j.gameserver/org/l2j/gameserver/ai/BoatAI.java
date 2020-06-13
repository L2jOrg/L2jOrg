/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.ai;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Boat;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.VehicleDeparture;
import org.l2j.gameserver.network.serverpackets.VehicleInfo;
import org.l2j.gameserver.network.serverpackets.VehicleStarted;

/**
 * @author DS
 */
public class BoatAI extends VehicleAI {
    public BoatAI(Boat boat) {
        super(boat);
    }

    @Override
    protected void moveTo(int x, int y, int z) {
        if (!actor.isMovementDisabled()) {
            if (!_clientMoving) {
                actor.broadcastPacket(new VehicleStarted(getActor(), 1));
            }

            _clientMoving = true;
            actor.moveToLocation(x, y, z, 0);
            actor.broadcastPacket(new VehicleDeparture(getActor()));
        }
    }

    @Override
    public void clientStopMoving(Location loc) {
        if (actor.isMoving()) {
            actor.stopMove(loc);
        }

        if (_clientMoving || (loc != null)) {
            _clientMoving = false;
            actor.broadcastPacket(new VehicleStarted(getActor(), 0));
            actor.broadcastPacket(new VehicleInfo(getActor()));
        }
    }

    @Override
    public void describeStateToPlayer(Player player) {
        if (_clientMoving) {
            player.sendPacket(new VehicleDeparture(getActor()));
        }
    }

    @Override
    public Boat getActor() {
        return (Boat) actor;
    }
}