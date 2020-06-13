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
 * The Class ConditionPlayerIsHero.
 *
 * @author JoeAlisson
 */
public class ConditionPlayerIsHero extends Condition {

    private static final ConditionPlayerIsHero HERO = new ConditionPlayerIsHero(true);
    private static final ConditionPlayerIsHero NO_HERO = new ConditionPlayerIsHero(false);

    public final boolean isHero;

    /**
     * Instantiates a new condition player is hero.
     *
     * @param isHero the val
     */
    private ConditionPlayerIsHero(boolean isHero) {
        this.isHero = isHero;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        if (effector.getActingPlayer() == null) {
            return false;
        }
        return (effector.getActingPlayer().isHero() == isHero);
    }

    public static ConditionPlayerIsHero of(boolean hero) {
        return hero ? HERO : NO_HERO;
    }
}
