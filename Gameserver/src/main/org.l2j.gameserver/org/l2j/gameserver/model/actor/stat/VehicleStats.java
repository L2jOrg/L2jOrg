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
package org.l2j.gameserver.model.actor.stat;

import org.l2j.gameserver.model.actor.Vehicle;

public class VehicleStats extends CreatureStats {
    private float _moveSpeed = 0;
    private int _rotationSpeed = 0;

    public VehicleStats(Vehicle activeChar) {
        super(activeChar);
    }

    @Override
    public double getMoveSpeed() {
        return _moveSpeed;
    }

    public final void setMoveSpeed(float speed) {
        _moveSpeed = speed;
    }

    public final double getRotationSpeed() {
        return _rotationSpeed;
    }

    public final void setRotationSpeed(int speed) {
        _rotationSpeed = speed;
    }
}