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

import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * The Class ConditionPlayerRace.
 *
 * @author mkizub, Zoey76
 */
public class ConditionPlayerRace extends Condition {
    private final Race[] _races;

    /**
     * Instantiates a new condition player race.
     *
     * @param races the list containing the allowed races.
     */
    public ConditionPlayerRace(Race[] races) {
        _races = races;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        if (!isPlayer(effector)) {
            return false;
        }
        return CommonUtil.contains(_races, effector.getActingPlayer().getRace());
    }
}
