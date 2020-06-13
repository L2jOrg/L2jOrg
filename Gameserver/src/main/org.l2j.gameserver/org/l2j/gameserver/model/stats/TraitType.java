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
package org.l2j.gameserver.model.stats;

import java.util.ArrayList;
import java.util.List;

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
    STAFF(1);

    private final int _type; // 1 = weapon, 2 = weakness, 3 = resistance

    private final static List<TraitType> _weaknesses = new ArrayList<>();
    static
    {
        _weaknesses.add(BUG_WEAKNESS);
        _weaknesses.add(ANIMAL_WEAKNESS);
        _weaknesses.add(PLANT_WEAKNESS);
        _weaknesses.add(BEAST_WEAKNESS);
        _weaknesses.add(DRAGON_WEAKNESS);
        _weaknesses.add(GIANT_WEAKNESS);
        _weaknesses.add(CONSTRUCT_WEAKNESS);
        _weaknesses.add(VALAKAS);
        _weaknesses.add(ANESTHESIA);
        _weaknesses.add(DEMONIC_WEAKNESS);
        _weaknesses.add(DIVINE_WEAKNESS);
        _weaknesses.add(ELEMENTAL_WEAKNESS);
        _weaknesses.add(FAIRY_WEAKNESS);
        _weaknesses.add(HUMAN_WEAKNESS);
        _weaknesses.add(HUMANOID_WEAKNESS);
        _weaknesses.add(UNDEAD_WEAKNESS);
        _weaknesses.add(EMBRYO_WEAKNESS);
        _weaknesses.add(SPIRIT_WEAKNESS);
    }

    public static List<TraitType> getAllWeakness()
    {
        return _weaknesses;
    }

    TraitType(int type) {
        _type = type;
    }

    public int getType() {
        return _type;
    }
}
