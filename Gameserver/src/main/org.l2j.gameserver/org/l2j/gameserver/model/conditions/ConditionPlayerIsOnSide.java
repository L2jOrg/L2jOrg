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
package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.CastleSide;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.item.ItemTemplate;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * The Class ConditionPlayerIsOnSide.
 *
 * @author St3eT
 */
public class ConditionPlayerIsOnSide extends Condition {
    private final CastleSide _side;

    /**
     * Instantiates a new condition player race.
     *
     * @param side the allowed Castle side.
     */
    public ConditionPlayerIsOnSide(CastleSide side) {
        _side = side;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        if (!isPlayer(effector)) {
            return false;
        }
        return effector.getActingPlayer().getPlayerSide() == _side;
    }
}
