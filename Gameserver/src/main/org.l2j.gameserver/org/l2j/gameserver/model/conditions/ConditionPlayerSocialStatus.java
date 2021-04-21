/*
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
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.base.SocialStatus;
import org.l2j.gameserver.model.item.ItemTemplate;

import static java.util.Objects.isNull;

/**
 * The Class ConditionPlayerPledgeClass.
 *
 * @author MrPoke
 */
public final class ConditionPlayerSocialStatus extends Condition {

    public final SocialStatus socialStatus;

    /**
     * Instantiates a new condition player pledge class.
     *
     * @param pledgeClass the pledge class
     */
    public ConditionPlayerSocialStatus(SocialStatus pledgeClass) {
        socialStatus = pledgeClass;
    }

    /**
     * Test impl.
     *
     * @return true, if successful
     */
    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        final var player = effector.getActingPlayer();
        if (isNull(player) || isNull(player.getClan())) {
            return false;
        }
        return player.getSocialStatus().compareTo(socialStatus) >= 0;
    }
}