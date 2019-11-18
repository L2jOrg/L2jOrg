package org.l2j.gameserver.model.items.type;

public enum ArmorType implements ItemType {
    NONE,
    LIGHT,
    HEAVY,
    MAGIC,
    SIGIL,

    // L2J CUSTOM
    SHIELD; // TODO move to Weapon

    final int _mask;

    /**
     * Constructor of the ArmorType.
     */
    ArmorType() {
        _mask = 1 << (ordinal() + WeaponType.values().length);
    }

    /**
     * @return the ID of the ArmorType after applying a mask.
     */
    @Override
    public int mask() {
        return _mask;
    }
}
