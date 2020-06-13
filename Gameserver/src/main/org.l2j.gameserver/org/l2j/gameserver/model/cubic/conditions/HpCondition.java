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
package org.l2j.gameserver.model.cubic.conditions;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.cubic.CubicInstance;

import static org.l2j.gameserver.util.GameUtils.isCreature;
import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * @author UnAfraid
 */
public class HpCondition implements ICubicCondition {
    private final HpConditionType _type;
    private final int _hpPer;

    public HpCondition(HpConditionType type, int hpPer) {
        _type = type;
        _hpPer = hpPer;
    }

    @Override
    public boolean test(CubicInstance cubic, Creature owner, WorldObject target) {
        if (isCreature(target)  || isDoor(target)) {
            final double hpPer = (isDoor(target) ? (Door) target : (Creature) target).getCurrentHpPercent();
            switch (_type) {
                case GREATER: {
                    return hpPer > _hpPer;
                }
                case LESSER: {
                    return hpPer < _hpPer;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " chance: " + _hpPer;
    }

    public enum HpConditionType {
        GREATER,
        LESSER;
    }
}
