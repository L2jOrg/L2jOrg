package org.l2j.gameserver.engine.elemental.api;

import org.l2j.gameserver.model.stats.Stats;

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

    public Stats getAttackStat() {
        return switch (this) {
            case EARTH -> Stats.ELEMENTAL_SPIRIT_EARTH_ATTACK;
            case WIND -> Stats.ELEMENTAL_SPIRIT_WIND_ATTACK;
            case FIRE -> Stats.ELEMENTAL_SPIRIT_FIRE_ATTACK;
            case WATER -> Stats.ELEMENTAL_SPIRIT_WATER_ATTACK;
            default -> null;
        };
    }

    public Stats getDefenseStat() {
        return switch (this) {
            case EARTH -> Stats.ELEMENTAL_SPIRIT_EARTH_DEFENSE;
            case WIND -> Stats.ELEMENTAL_SPIRIT_WIND_DEFENSE;
            case FIRE -> Stats.ELEMENTAL_SPIRIT_FIRE_DEFENSE;
            case WATER -> Stats.ELEMENTAL_SPIRIT_WATER_DEFENSE;
            default -> null;
        };
    }
}