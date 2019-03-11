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
package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.items.L2Item;
import org.l2j.gameserver.model.skills.Skill;

/**
 * The Class ConditionPlayerSiegeSide.
 */
public class ConditionPlayerSiegeSide extends Condition {
    private final int _siegeSide;

    /**
     * Instantiates a new condition player siege side.
     *
     * @param side the side
     */
    public ConditionPlayerSiegeSide(int side) {
        _siegeSide = side;
    }

    @Override
    public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item) {
        if (effector.getActingPlayer() == null) {
            return false;
        }
        return effector.getActingPlayer().getSiegeSide() == _siegeSide;
    }
}
