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
package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.world.WorldTimeController;

/**
 * The Class ConditionGameTime.
 *
 * @author mkizub
 */
public class ConditionGameTime extends Condition {
    private final CheckGameTime _check;
    private final boolean _required;
    /**
     * Instantiates a new condition game time.
     *
     * @param check    the check
     * @param required the required
     */
    public ConditionGameTime(CheckGameTime check, boolean required) {
        _check = check;
        _required = required;
    }

    /**
     * Test impl.
     *
     * @return true, if successful
     */
    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        switch (_check) {
            case NIGHT: {
                return WorldTimeController.getInstance().isNight() == _required;
            }
        }
        return !_required;
    }

    /**
     * The Enum CheckGameTime.
     */
    public enum CheckGameTime {
        NIGHT
    }
}
