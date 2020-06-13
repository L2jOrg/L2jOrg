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
 * The Class ConditionPlayerSex.
 *
 * @author JoeAlisson
 */
public class ConditionPlayerSex extends Condition {

    private static final ConditionPlayerSex MALE = new ConditionPlayerSex(0);
    private static final ConditionPlayerSex FEMALE = new ConditionPlayerSex(1);

    // male 0 female 1
    public final int _sex;

    /**
     * Instantiates a new condition player sex.
     *
     * @param sex the sex
     */
    private ConditionPlayerSex(int sex) {
        _sex = sex;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        if (effector.getActingPlayer() == null) {
            return false;
        }
        return (effector.getActingPlayer().getAppearance().isFemale() ? 1 : 0) == _sex;
    }

    public static ConditionPlayerSex of(int sex) {
        return sex == 0 ? MALE : FEMALE;
    }
}
