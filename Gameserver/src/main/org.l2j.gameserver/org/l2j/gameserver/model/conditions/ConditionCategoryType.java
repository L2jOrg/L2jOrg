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

import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;

import java.util.Set;

/**
 * Condition Category Type implementation.
 *
 * @author Adry_85
 */
public class ConditionCategoryType extends Condition {
    private final Set<CategoryType> _categoryTypes;

    public ConditionCategoryType(Set<CategoryType> categoryTypes) {
        _categoryTypes = categoryTypes;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        for (CategoryType type : _categoryTypes) {
            if (effector.isInCategory(type)) {
                return true;
            }
        }
        return false;
    }
}
