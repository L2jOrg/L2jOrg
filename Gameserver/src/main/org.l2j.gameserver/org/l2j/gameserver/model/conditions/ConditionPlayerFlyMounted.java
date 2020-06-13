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
package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.item.ItemTemplate;

/**
 * The Class ConditionPlayerFlyMounted.
 *
 * @author kerberos
 * @author JoeAlisson
 */
public class ConditionPlayerFlyMounted extends Condition {

    private static final ConditionPlayerFlyMounted FLYING = new ConditionPlayerFlyMounted(true);
    private static final ConditionPlayerFlyMounted NO_FLYING = new ConditionPlayerFlyMounted(false);

    public final boolean _val;

    /**
     * Instantiates a new condition player fly mounted.
     *
     * @param val the val
     */
    private ConditionPlayerFlyMounted(boolean val) {
        _val = val;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        return (effector.getActingPlayer() == null) || (effector.getActingPlayer().isFlyingMounted() == _val);
    }

    public static ConditionPlayerFlyMounted of(boolean flying) {
        return flying ? FLYING : NO_FLYING;
    }
}
