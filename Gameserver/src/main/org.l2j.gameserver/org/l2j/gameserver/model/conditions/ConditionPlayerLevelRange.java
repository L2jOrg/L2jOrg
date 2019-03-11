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
 * @author Zoey76
 */
public class ConditionPlayerLevelRange extends Condition {
    private final int[] _levels;

    /**
     * Instantiates a new condition player levels range.
     *
     * @param levels the {@code levels} range.
     */
    public ConditionPlayerLevelRange(int[] levels) {
        _levels = levels;
    }

    @Override
    public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item) {
        final int level = effector.getLevel();
        return ((level >= _levels[0]) && (level <= _levels[1]));
    }
}
