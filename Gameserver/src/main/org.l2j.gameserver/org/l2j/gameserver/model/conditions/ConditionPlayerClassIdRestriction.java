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

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;

import java.util.List;

/**
 * The Class ConditionPlayerClassIdRestriction.
 */
public class ConditionPlayerClassIdRestriction extends Condition {
    private final List<Integer> _classIds;

    /**
     * Instantiates a new condition player class id restriction.
     *
     * @param classId the class id
     */
    public ConditionPlayerClassIdRestriction(List<Integer> classId) {
        _classIds = classId;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        return (effector.getActingPlayer() != null) && (_classIds.contains(effector.getActingPlayer().getClassId().getId()));
    }
}
