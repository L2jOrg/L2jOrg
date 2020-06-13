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
package org.l2j.gameserver.model.item.type;

import org.l2j.gameserver.model.stats.TraitType;

/**
 * Weapon Type enumerated.
 *
 * @author mkizub
 * @author JoeAlisson
 */
public enum WeaponType implements ItemType {
    NONE(TraitType.NONE),
    SWORD(TraitType.SWORD),
    TWO_HAND_SWORD(TraitType.TWO_HAND_SWORD),
    MAGIC_SWORD(TraitType.MAGIC_SWORD),
    BLUNT(TraitType.BLUNT),
    HAMMER(TraitType.HAMMER),
    ROD(TraitType.ROD),
    STAFF(TraitType.STAFF),
    DAGGER(TraitType.DAGGER),
    SPEAR(TraitType.SPEAR),
    FIST(TraitType.FIST),
    BOW(TraitType.BOW),
    ETC(TraitType.ETC),
    DUAL(TraitType.DUAL),
    DUAL_FIST(TraitType.DUALFIST),
    FISHING_ROD(TraitType.NONE),
    RAPIER(TraitType.RAPIER),
    CROSSBOW(TraitType.CROSSBOW),
    ANCIENT_SWORD(TraitType.ANCIENT_SWORD),
    FLAG(TraitType.NONE),
    DUAL_DAGGER(TraitType.DUAL_DAGGER),
    OWN_THING(TraitType.NONE),
    TWO_HAND_CROSSBOW(TraitType.TWO_HAND_CROSSBOW),
    DUAL_BLUNT(TraitType.DUAL_BLUNT);

    private final int _mask;
    private final TraitType _traitType;

    /**
     * Constructor of the L2WeaponType.
     *
     * @param traitType
     */
    WeaponType(TraitType traitType) {
        _mask = 1 << ordinal();
        _traitType = traitType;
    }

    /**
     * @return the ID of the item after applying the mask.
     */
    @Override
    public int mask() {
        return _mask;
    }

    /**
     * @return L2TraitType the type of the WeaponType
     */
    public TraitType getTraitType() {
        return _traitType;
    }

    public boolean isRanged() {
        return (this == BOW) || (this == CROSSBOW) || (this == TWO_HAND_CROSSBOW);
    }

    public boolean isCrossbow() {
        return (this == CROSSBOW) || (this == TWO_HAND_CROSSBOW);
    }

    public boolean isDual() {
        return (this == FIST) || (this == DUAL) || (this == DUAL_DAGGER) || (this == DUAL_BLUNT);
    }
}
