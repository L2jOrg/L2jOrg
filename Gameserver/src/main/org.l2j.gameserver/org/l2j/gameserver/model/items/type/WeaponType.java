package org.l2j.gameserver.model.items.type;

import org.l2j.gameserver.model.stats.TraitType;

/**
 * Weapon Type enumerated.
 *
 * @author mkizub
 * @author JoeAlisson
 */
public enum WeaponType implements ItemType {
    NONE(TraitType.NONE), // TODO should be Shield
    SHIELD(TraitType.NONE),
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
    FISHING_ROD(TraitType.NONE),
    RAPIER(TraitType.RAPIER),
    CROSSBOW(TraitType.CROSSBOW),
    ANCIENT_SWORD(TraitType.ANCIENT_SWORD),
    DUAL_DAGGER(TraitType.DUAL_DAGGER),
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
