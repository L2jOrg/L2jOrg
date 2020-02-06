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

/**
 * The Class ConditionPlayerIsPvpFlagged.
 *
 * @author Mobius
 */
public class ConditionPlayerIsPvpFlagged extends Condition {
    private final boolean _val;

    /**
     * Instantiates a new condition player is PvP flagged.
     *
     * @param val the val
     */
    public ConditionPlayerIsPvpFlagged(boolean val) {
        _val = val;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        return (effector.getActingPlayer() != null) && ((effector.getActingPlayer().getPvpFlag() > 0) == _val);
    }
}
