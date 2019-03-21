package org.l2j.gameserver.model.items.type;

import org.l2j.gameserver.model.stats.TraitType;

/**
 * Weapon Type enumerated.
 *
 * @author mkizub
 */
public enum WeaponType implements ItemType {
    NONE(TraitType.NONE),
    SWORD(TraitType.SWORD),
    BLUNT(TraitType.BLUNT),
    DAGGER(TraitType.DAGGER),
    POLE(TraitType.POLE),
    DUALFIST(TraitType.DUALFIST),
    BOW(TraitType.BOW),
    ETC(TraitType.ETC),
    DUAL(TraitType.DUAL),
    FIST(TraitType.FIST), // 0 items with that type
    FISHINGROD(TraitType.NONE),
    FLAG(TraitType.NONE); // 0 items with that type

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
        return this == BOW;
    }

    public boolean isDual() {
        return (this == DUALFIST) || (this == DUAL);
    }
}
