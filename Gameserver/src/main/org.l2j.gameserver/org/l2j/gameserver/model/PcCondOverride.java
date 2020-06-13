/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model;

/**
 * @author UnAfraid
 */
public enum PcCondOverride {
    MAX_STATS_VALUE(0, "Overrides maximum states conditions"),
    ITEM_CONDITIONS(1, "Overrides item usage conditions"),
    SKILL_CONDITIONS(2, "Overrides skill usage conditions"),
    ZONE_CONDITIONS(3, "Overrides zone conditions"),
    CASTLE_CONDITIONS(4, "Overrides castle conditions"),
    FORTRESS_CONDITIONS(5, "Overrides fortress conditions"),
    CLANHALL_CONDITIONS(6, "Overrides clan hall conditions"),
    FLOOD_CONDITIONS(7, "Overrides floods conditions"),
    CHAT_CONDITIONS(8, "Overrides chat conditions"),
    INSTANCE_CONDITIONS(9, "Overrides instance conditions"),
    QUEST_CONDITIONS(10, "Overrides quest conditions"),
    DEATH_PENALTY(11, "Overrides death penalty conditions"),
    DESTROY_ALL_ITEMS(12, "Overrides item destroy conditions"),
    SEE_ALL_PLAYERS(13, "Overrides the conditions to see hidden players"),
    TARGET_ALL(14, "Overrides target conditions"),
    DROP_ALL_ITEMS(15, "Overrides item drop conditions");

    private final int _mask;
    private final String _descr;

    PcCondOverride(int id, String descr) {
        _mask = 1 << id;
        _descr = descr;
    }

    public static PcCondOverride getCondOverride(int ordinal) {
        try {
            return values()[ordinal];
        } catch (Exception e) {
            return null;
        }
    }

    public static long getAllExceptionsMask() {
        long result = 0;
        for (PcCondOverride ex : values()) {
            result |= ex.getMask();
        }
        return result;
    }

    public int getMask() {
        return _mask;
    }

    public String getDescription() {
        return _descr;
    }
}
