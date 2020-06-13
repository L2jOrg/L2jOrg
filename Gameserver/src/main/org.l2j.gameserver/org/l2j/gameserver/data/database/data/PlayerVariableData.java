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
package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

/**
 * @author JoeAlisson
 */
@Table("player_variables")
public class PlayerVariableData {

    public static final int REVENGE_USABLE_FUNCTIONS = 5;

    @Column("player_id")
    private int playerId;

    @Column("revenge_teleports")
    private byte revengeTeleports;

    @Column("revenge_locations")
    private byte revengeLocations;

    public byte getRevengeTeleports() {
        return revengeTeleports;
    }

    public byte getRevengeLocations() {
        return revengeLocations;
    }

    public static PlayerVariableData init(int playerId) {
        var data = new PlayerVariableData();
        data.revengeTeleports = REVENGE_USABLE_FUNCTIONS;
        data.revengeLocations = REVENGE_USABLE_FUNCTIONS;
        data.playerId = playerId;
        return data;
    }

    public void useRevengeLocation() {
        revengeLocations--;
    }

    public void useRevengeTeleport() {
        revengeTeleports--;
    }

    public void resetRevengeData() {
        revengeTeleports = REVENGE_USABLE_FUNCTIONS;
        revengeLocations = REVENGE_USABLE_FUNCTIONS;
    }
}
