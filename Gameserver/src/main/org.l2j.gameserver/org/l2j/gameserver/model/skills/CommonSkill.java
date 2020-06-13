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
package org.l2j.gameserver.model.skills;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.holders.SkillHolder;

/**
 * An Enum to hold some important references to commonly used skills
 *
 * @author DrHouse
 * @author JoeAlisson
 */
public enum CommonSkill {
    CREATE_DWARVEN(172, 1),
    CREATE_CRITICAL(53001,1),
    CREATE_MASTER(53002,1),
    LUCKY(194, 1),
    EXPERTISE(239, 1),
    SEAL_OF_RULER(246, 1),
    BUILD_HEADQUARTERS(247, 1),
    CRYSTALLIZE(248, 1),
    SHADOW_SENSE_ID(294, 1),
    STRIDER_SIEGE_ASSAULT(325, 1),
    BUILD_ADVANCED_HEADQUARTERS(326, 1),
    SHINE_SIDE(397, 1),
    SHADOW_SIDE(398, 1),
    ONYX_BEAST_TRANSFORMATION(617, 1),
    OUTPOST_CONSTRUCTION(844, 1),
    OUTPOST_DEMOLITION(845, 1),
    HIDE(922, 4),
    CREATE_COMMON(1320, 1),
    DIVINE_INSPIRATION(1405, 1),
    LARGE_FIREWORK(2025, 1),
    SPECIAL_TREE_RECOVERY_BONUS(2139, 1),
    CARAVANS_SECRET_MEDICINE(2341, 1),
    VOID_BURST(3630, 1),
    VOID_FLOW(3631, 1),
    WEIGHT_PENALTY(4270, 1),
    RAID_CURSE(4215, 1),
    WYVERN_BREATH(4289, 1),
    RAID_CURSE2(4515, 1),
    THE_VICTOR_OF_WAR(5074, 1),
    THE_VANQUISHED_OF_WAR(5075, 1),
    BLESSING_OF_PROTECTION(5182, 1),
    FIREWORK(5965, 1),
    PET_SWITCH_STANCE(6054, 1),
    WEAPON_GRADE_PENALTY(6209, 1),
    ARMOR_GRADE_PENALTY(6213, 1),
    HAIR_ACCESSORY_SET(17192, 1),
    ALCHEMY_CUBE(17943, 1),
    ALCHEMY_CUBE_RANDOM_SUCCESS(17966, 1),
    CLAN_ADVENT(19009, 1),
    ABILITY_OF_LIGHT(19032, 1),
    ABILITY_OF_DARKNESS(19033, 1),
    IMPRIT_OF_LIGHT(19034, 1),
    IMPRIT_OF_DARKNESS(19035, 1),
    SHINE_MASTERY(45178, 1),
    SHADOW_MASTERY(45179, 1),
    BOT_REPORT_STATUS(55031, 1),
    RANKER_FIRST_CLASS(60003, 1),
    RANKER_SECOND_CLASS(60004, 1),
    RANKER_THIRD_CLASS(60005, 1),
    RANKER_HUMAN(60006, 1),
    RANKER_ELF(60007, 1),
    RANKER_DARK_ELF(60008, 1),
    RANKER_ORC(60009, 1),
    RANKER_DWARF(60010, 1),
    RANKER_JIN_KAMAEL(60011, 1),
    RANKER_BENEFIT_I(60012, 1),
    RANKER_BENEFIT_II(60013, 1),
    RANKER_BENEFIT_III(60014, 1),
    RANKER_RACE_BENEFIT(60015, 1),
    TELEPORT(60018, 1);

    private final SkillHolder _holder;

    CommonSkill(int id, int level) {
        _holder = new SkillHolder(id, level);
    }

    public int getId() {
        return _holder.getSkillId();
    }

    public int getLevel() {
        return _holder.getLevel();
    }

    public Skill getSkill() {
        return _holder.getSkill();
    }

    public boolean isSame(int skillId) {
        return  skillId == _holder.getSkillId();
    }
}
