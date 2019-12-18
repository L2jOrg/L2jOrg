package org.l2j.gameserver.api.elemental;

import org.l2j.gameserver.model.stats.Stat;

/**
 * @author JoeAlisson
 */
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

    public boolean isSuperior(ElementalType targetType) {
        return this == superior(targetType);
    }

    public boolean isInferior(ElementalType targetType) {
        return targetType == superior(this);
    }

    public ElementalType getSuperior() {
        return superior(this);
    }

    public static ElementalType superior(ElementalType elementalType) {
        return switch (elementalType) {
            case FIRE -> WATER;
            case WATER -> WIND;
            case WIND -> EARTH;
            case EARTH -> FIRE;
            default -> NONE;
        };
    }

    public Stat getAttackStat() {
        return switch (this) {
            case EARTH -> Stat.ELEMENTAL_SPIRIT_EARTH_ATTACK;
            case WIND -> Stat.ELEMENTAL_SPIRIT_WIND_ATTACK;
            case FIRE -> Stat.ELEMENTAL_SPIRIT_FIRE_ATTACK;
            case WATER -> Stat.ELEMENTAL_SPIRIT_WATER_ATTACK;
            default -> null;
        };
    }

    public Stat getDefenseStat() {
        return switch (this) {
            case EARTH -> Stat.ELEMENTAL_SPIRIT_EARTH_DEFENSE;
            case WIND -> Stat.ELEMENTAL_SPIRIT_WIND_DEFENSE;
            case FIRE -> Stat.ELEMENTAL_SPIRIT_FIRE_DEFENSE;
            case WATER -> Stat.ELEMENTAL_SPIRIT_WATER_DEFENSE;
            default -> null;
        };
    }
}