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
package org.l2j.gameserver.model.stats;

import java.util.EnumSet;

/**
 * @author UnAfraid, NosBit
 * @author JoeAlisson
 */
public enum TraitType {
    NONE(0),
    SWORD(1),
    BLUNT(1),
    DAGGER(1),
    SPEAR(1),
    FIST(1),
    BOW(1),
    HAMMER(1),
    ETC(1),
    UNK_8(0),
    POISON(3),
    HOLD(3),
    BLEED(3),
    SLEEP(3),
    SHOCK(3),
    DERANGEMENT(3),
    BUG_WEAKNESS(2),
    ANIMAL_WEAKNESS(2),
    PLANT_WEAKNESS(2),
    BEAST_WEAKNESS(2),
    DRAGON_WEAKNESS(2),
    PARALYZE(3),
    DUAL(1),
    TWO_HAND_SWORD(1),
    MAGIC_SWORD(1),
    DUALFIST(1),
    BOSS(3),
    GIANT_WEAKNESS(2),
    CONSTRUCT_WEAKNESS(2),
    DEATH(3),
    VALAKAS(2),
    ANESTHESIA(2),
    CRITICAL_POISON(3),
    ROOT_PHYSICALLY(3),
    ROOT_MAGICALLY(3),
    RAPIER(1),
    CROSSBOW(1),
    ANCIENT_SWORD(1),
    TURN_STONE(3),
    GUST(3),
    PHYSICAL_BLOCKADE(3),
    TARGET(3),
    PHYSICAL_WEAKNESS(3),
    MAGICAL_WEAKNESS(3),
    DUAL_DAGGER(1),
    DEMONIC_WEAKNESS(2), // CT26_P4
    DIVINE_WEAKNESS(2),
    ELEMENTAL_WEAKNESS(2),
    FAIRY_WEAKNESS(2),
    HUMAN_WEAKNESS(2),
    HUMANOID_WEAKNESS(2),
    UNDEAD_WEAKNESS(2),
    DUAL_BLUNT(1),
    KNOCKBACK(3),
    KNOCKDOWN(3),
    PULL(3),
    HATE(3),
    AGGRESSION(3),
    AIRBIND(3),
    DISARM(3),
    DEPORT(3),
    CHANGEBODY(3),
    TWO_HAND_CROSSBOW(1),
    ZONE(3),
    PSYCHIC(3),
    EMBRYO_WEAKNESS(2),
    SPIRIT_WEAKNESS(2),
    ROD(1),
    STAFF(1),
    PISTOLS(1);

    private final int _type; // 1 = weapon, 2 = weakness, 3 = resistance

    private final static EnumSet<TraitType> WEAKNESSES = EnumSet.of(
        BUG_WEAKNESS,
        ANIMAL_WEAKNESS,
        PLANT_WEAKNESS,
        BEAST_WEAKNESS,
        DRAGON_WEAKNESS,
        GIANT_WEAKNESS,
        CONSTRUCT_WEAKNESS,
        VALAKAS,
        ANESTHESIA,
        DEMONIC_WEAKNESS,
        DIVINE_WEAKNESS,
        ELEMENTAL_WEAKNESS,
        FAIRY_WEAKNESS,
        HUMAN_WEAKNESS,
        HUMANOID_WEAKNESS,
        UNDEAD_WEAKNESS,
        EMBRYO_WEAKNESS,
        SPIRIT_WEAKNESS
    );

    private static final TraitType[] CACHE = values();

    TraitType(int type) {
        _type = type;
    }

    public int getType() {
        return _type;
    }

    public static EnumSet<TraitType> getAllWeakness()
    {
        return WEAKNESSES;
    }

    public static TraitType[] all() {
        return CACHE;
    }
}
