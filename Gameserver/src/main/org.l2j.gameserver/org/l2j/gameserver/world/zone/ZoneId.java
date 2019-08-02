/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.world.zone;

/**
 * Zone Ids.
 *
 * @author Zoey76
 */
public enum ZoneId {
    PVP,
    PEACE,
    SIEGE,
    MOTHER_TREE,
    CLAN_HALL,
    LANDING,
    NO_LANDING,
    WATER,
    JAIL,
    MONSTER_TRACK,
    CASTLE,
    SWAMP,
    NO_SUMMON_FRIEND,
    FORT,
    NO_STORE,
    SCRIPT,
    HQ,
    DANGER_AREA,
    ALTERED,
    NO_BOOKMARK,
    NO_ITEM_DROP,
    NO_RESTART,
    FISHING,
    UNDYING,
    TAX;

    public static int getZoneCount() {
        return values().length;
    }
}
