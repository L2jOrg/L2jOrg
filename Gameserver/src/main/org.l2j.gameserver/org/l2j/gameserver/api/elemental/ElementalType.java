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