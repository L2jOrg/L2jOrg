/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.ai;

import org.l2j.gameserver.mobius.gameserver.model.Location;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2BoatInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.VehicleDeparture;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.VehicleInfo;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.VehicleStarted;

/**
 * @author DS
 */
public class L2BoatAI extends L2VehicleAI {
    public L2BoatAI(L2BoatInstance boat) {
        super(boat);
    }

    @Override
    protected void moveTo(int x, int y, int z) {
        if (!_actor.isMovementDisabled()) {
            if (!_clientMoving) {
                _actor.broadcastPacket(new VehicleStarted(getActor(), 1));
            }

            _clientMoving = true;
            _actor.moveToLocation(x, y, z, 0);
            _actor.broadcastPacket(new VehicleDeparture(getActor()));
        }
    }

    @Override
    public void clientStopMoving(Location loc) {
        if (_actor.isMoving()) {
            _actor.stopMove(loc);
        }

        if (_clientMoving || (loc != null)) {
            _clientMoving = false;
            _actor.broadcastPacket(new VehicleStarted(getActor(), 0));
            _actor.broadcastPacket(new VehicleInfo(getActor()));
        }
    }

    @Override
    public void describeStateToPlayer(L2PcInstance player) {
        if (_clientMoving) {
            player.sendPacket(new VehicleDeparture(getActor()));
        }
    }

    @Override
    public L2BoatInstance getActor() {
        return (L2BoatInstance) _actor;
    }
}