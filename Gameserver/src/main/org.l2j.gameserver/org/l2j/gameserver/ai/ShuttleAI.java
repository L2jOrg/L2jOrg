/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.ai;

import org.l2j.gameserver.model.actor.instance.Shuttle;
import org.l2j.gameserver.network.serverpackets.shuttle.ExShuttleMove;

/**
 * @author UnAfraid
 */
public class ShuttleAI extends VehicleAI {
    public ShuttleAI(Shuttle shuttle) {
        super(shuttle);
    }

    @Override
    public void moveTo(int x, int y, int z) {
        if (!actor.isMovementDisabled()) {
            _clientMoving = true;
            actor.moveToLocation(x, y, z, 0);
            actor.broadcastPacket(new ExShuttleMove(getActor(), x, y, z));
        }
    }

    @Override
    public Shuttle getActor() {
        return (Shuttle) actor;
    }
}
