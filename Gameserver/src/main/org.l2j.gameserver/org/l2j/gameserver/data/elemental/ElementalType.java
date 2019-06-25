package org.l2j.gameserver.data.elemental;

public enum ElementalType {
    NONE,
    FIRE,
    WATER,
    WIND,
    EARTH;

    public byte getId() {
        return (byte) (ordinal());
    }

    public static ElementalType of(byte elementId) {
        return values()[elementId];
    }

    public ElementalType getDominating() {
        return dominating(this);
    }

    public ElementalType dominating(ElementalType elementalType) {
        return switch (elementalType) {
            case FIRE -> WATER;
            case WATER -> EARTH;
            case WIND ->  FIRE;
            case EARTH -> WIND;
            default -> NONE;
        };
    }


}
