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
package org.l2j.gameserver.model.options;

import org.l2j.gameserver.model.holders.SkillHolder;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class OptionsSkillHolder extends SkillHolder {
    private final OptionsSkillType type;
    private final double chance;

    public OptionsSkillHolder(SkillHolder skill, double chance, OptionsSkillType type) {
        super(skill.getSkillId(), skill.getLevel());
        this.chance = chance;
        this.type = type;
    }

    public OptionsSkillType getSkillType() {
        return type;
    }

    public double getChance() {
        return chance;
    }
}
