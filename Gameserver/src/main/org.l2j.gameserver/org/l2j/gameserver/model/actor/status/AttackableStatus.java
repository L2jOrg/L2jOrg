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
package org.l2j.gameserver.model.actor.status;

import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;

public class AttackableStatus extends NpcStatus {
    public AttackableStatus(Attackable activeChar) {
        super(activeChar);
    }

    @Override
    public final void reduceHp(double value, Creature attacker) {
        reduceHp(value, attacker, true, false, false);
    }

    @Override
    public final void reduceHp(double value, Creature attacker, boolean awake, boolean isDOT, boolean isHpConsumption) {
        if (getOwner().isDead()) {
            return;
        }

        if (value > 0) {
            if (getOwner().isOverhit()) {
                getOwner().setOverhitValues(attacker, value);
            } else {
                getOwner().overhitEnabled(false);
            }
        } else {
            getOwner().overhitEnabled(false);
        }

        super.reduceHp(value, attacker, awake, isDOT, isHpConsumption);

        if (!getOwner().isDead()) {
            // And the attacker's hit didn't kill the mob, clear the over-hit flag
            getOwner().overhitEnabled(false);
        }
    }

    @Override
    public boolean setCurrentHp(double newHp, boolean broadcastPacket) {
        return super.setCurrentHp(newHp, true);
    }

    @Override
    public Attackable getOwner() {
        return (Attackable) super.getOwner();
    }
}