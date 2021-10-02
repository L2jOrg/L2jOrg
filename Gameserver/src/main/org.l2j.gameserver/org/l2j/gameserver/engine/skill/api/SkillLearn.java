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
package org.l2j.gameserver.engine.skill.api;

import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.base.SocialStatus;
import org.l2j.gameserver.model.holders.ItemHolder;

import java.util.List;
import java.util.Set;

/**
 * @author JoeAlisson
 */
public record SkillLearn(
        int id,
        int level,
        int requiredLevel,
        boolean autoLearn,
        long sp,
        boolean learnedByNpc,
        SocialStatus socialStatus,
        Set<Race> races,
        List<ItemHolder> requiredItems,
        List<Skill> replaceSkills,
        IntSet residences) {

    public boolean isResidential() {
        return !residences.isEmpty();
    }

    public Skill getSkill() {
        return SkillEngine.getInstance().getSkill(id, level);
    }
}
