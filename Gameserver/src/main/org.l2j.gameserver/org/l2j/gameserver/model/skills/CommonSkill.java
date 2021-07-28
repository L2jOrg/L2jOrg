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
package org.l2j.gameserver.model.skills;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;

/**
 * An Enum to hold some important references to commonly used skills
 *
 * @author DrHouse
 * @author JoeAlisson
 */
public enum CommonSkill {
    CREATE_DWARVEN(172),
    LUCKY(194),
    EXPERTISE(239),
    SEAL_OF_RULER(246),
    BUILD_HEADQUARTERS(247),
    CRYSTALLIZE(248),
    SHADOW_SENSE_ID(294),
    BUILD_ADVANCED_HEADQUARTERS(326),
    SHINE_SIDE(397),
    SHADOW_SIDE(398),
    ONYX_BEAST_TRANSFORMATION(617),
    OUTPOST_CONSTRUCTION(844),
    OUTPOST_DEMOLITION(845),
    HIDE(922),
    CREATE_COMMON(1320),
    DIVINE_INSPIRATION(1405),
    ARCANE_SHIELD(1556),
    LARGE_FIREWORK(2025),
    SUMMON_PET(2046),
    SPIRITSHOT(2061),
    SPECIAL_TREE_RECOVERY_BONUS(2139),
    SOULSHOT(2154),
    CARAVANS_SECRET_MEDICINE(2341),
    VOID_BURST(3630),
    VOID_FLOW(3631),
    WEIGHT_PENALTY(4270),
    RAID_CURSE(4215),
    WYVERN_BREATH(4289),
    RAID_CURSE2(4515),
    THE_VICTOR_OF_WAR(5074),
    THE_VANQUISHED_OF_WAR(5075),
    BLESSING_OF_PROTECTION(5182),
    BATTLEGROUND_DEATH_SYNDROME(5660),
    FIREWORK(5965),
    PET_SWITCH_STANCE(6054),
    WEAPON_GRADE_PENALTY(6209),
    ARMOR_GRADE_PENALTY(6213),
    HAIR_ACCESSORY_SET(17192),
    ALCHEMY_CUBE(17943),
    ALCHEMY_CUBE_RANDOM_SUCCESS(17966),
    CLAN_ADVENT(19009),
    ABILITY_OF_LIGHT(19032),
    ABILITY_OF_DARKNESS(19033),
    IMPRIT_OF_LIGHT(19034),
    IMPRIT_OF_DARKNESS(19035),
    SHINE_MASTERY(45178),
    SHADOW_MASTERY(45179),
    CREATE_CRITICAL(53001),
    CREATE_MASTER(53002),
    RANKER_HUMAN_TRANSFORM(54204),
    RANKER_JIN_KAMAEL_TRANSFORM(54205),
    RANKER_ORC_TRANSFORM(54209),
    RANKER_ELF_TRANSFORM(54210),
    RANKER_DARK_ELF_TRANSFORM(54211),
    RANKER_DWARF_TRANSFORM(54212),
    BOT_REPORT_STATUS(55031),
    RANKER_FIRST_CLASS(60003),
    RANKER_SECOND_CLASS(60004),
    RANKER_THIRD_CLASS(60005),
    RANKER_HUMAN(60006),
    RANKER_ELF(60007),
    RANKER_DARK_ELF(60008),
    RANKER_ORC(60009),
    RANKER_DWARF(60010),
    RANKER_JIN_KAMAEL(60011),
    REPUTATION_1(60002),
    REPUTATION_2(60003),
    REPUTATION_3(60004),
    TELEPORT(60018);

    private final int id;
    private Skill skill;

    CommonSkill(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Skill getSkill() {
        if(skill == null) {
            skill = SkillEngine.getInstance().getSkill(id, 1);
        }
        return skill;
    }

}
