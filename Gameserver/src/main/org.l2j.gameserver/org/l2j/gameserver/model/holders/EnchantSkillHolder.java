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
package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.enums.SkillEnchantType;
import org.l2j.gameserver.model.StatsSet;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Sdw
 */
public class EnchantSkillHolder {
    private final Map<SkillEnchantType, Long> _sp = new EnumMap<>(SkillEnchantType.class);
    private final Map<SkillEnchantType, Integer> _chance = new EnumMap<>(SkillEnchantType.class);
    private final Map<SkillEnchantType, Set<ItemHolder>> _requiredItems = new EnumMap<>(SkillEnchantType.class);

    public EnchantSkillHolder(StatsSet set) {
    }

    public long getSp(SkillEnchantType type) {
        return _sp.getOrDefault(type, 0L);
    }

    public int getChance(SkillEnchantType type) {
        return _chance.getOrDefault(type, 100);
    }

    public Set<ItemHolder> getRequiredItems(SkillEnchantType type) {
        return _requiredItems.getOrDefault(type, Collections.emptySet());
    }
}
