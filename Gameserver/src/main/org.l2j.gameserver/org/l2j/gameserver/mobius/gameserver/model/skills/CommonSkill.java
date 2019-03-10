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
package org.l2j.gameserver.mobius.gameserver.model.skills;

import org.l2j.gameserver.mobius.gameserver.model.holders.SkillHolder;

/**
 * An Enum to hold some important references to commonly used skills
 *
 * @author DrHouse
 */
public enum CommonSkill {
    RAID_CURSE(4215, 1),
    RAID_CURSE2(4515, 1),
    SEAL_OF_RULER(246, 1),
    BUILD_HEADQUARTERS(247, 1),
    WYVERN_BREATH(4289, 1),
    STRIDER_SIEGE_ASSAULT(325, 1),
    FIREWORK(5965, 1),
    LARGE_FIREWORK(2025, 1),
    BLESSING_OF_PROTECTION(5182, 1),
    VOID_BURST(3630, 1),
    VOID_FLOW(3631, 1),
    THE_VICTOR_OF_WAR(5074, 1),
    THE_VANQUISHED_OF_WAR(5075, 1),
    SPECIAL_TREE_RECOVERY_BONUS(2139, 1),
    WEAPON_GRADE_PENALTY(6209, 1),
    ARMOR_GRADE_PENALTY(6213, 1),
    CREATE_DWARVEN(172, 1),
    LUCKY(194, 1),
    EXPERTISE(239, 1),
    CRYSTALLIZE(248, 1),
    ONYX_BEAST_TRANSFORMATION(617, 1),
    CREATE_COMMON(1320, 1),
    DIVINE_INSPIRATION(1405, 1),
    CARAVANS_SECRET_MEDICINE(2341, 1),
    IMPRIT_OF_LIGHT(19034, 1),
    IMPRIT_OF_DARKNESS(19035, 1),
    ABILITY_OF_LIGHT(19032, 1),
    ABILITY_OF_DARKNESS(19033, 1),
    CLAN_ADVENT(19009, 1),
    HAIR_ACCESSORY_SET(17192, 1),
    ALCHEMY_CUBE(17943, 1),
    ALCHEMY_CUBE_RANDOM_SUCCESS(17966, 1),
    PET_SWITCH_STANCE(6054, 1),
    WEIGHT_PENALTY(4270, 1);

    private final SkillHolder _holder;

    CommonSkill(int id, int level) {
        _holder = new SkillHolder(id, level);
    }

    public int getId() {
        return _holder.getSkillId();
    }

    public int getLevel() {
        return _holder.getSkillLevel();
    }

    public Skill getSkill() {
        return _holder.getSkill();
    }
}
