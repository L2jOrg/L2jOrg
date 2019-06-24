package org.l2j.gameserver.data.elemental;

public enum ElementalType {
    FIRE,
    WATER,
    WIND,
    EARTH;

    public byte getId() {
        return (byte) (ordinal() + 1);
    }

    public static ElementalType of(byte elementId) {
        return values()[elementId -1];
    }
}
