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
package org.l2j.gameserver.enums;

/**
 * @author Nik
 * @author joeAlisson
 */
public enum SpecialItemType {
    PC_CAFE_POINTS(-100, "Player Commendation Points"),
    CLAN_REPUTATION(-200, "Clan Reputation Points"),
    FAME(-300, "Fame"),
    FIELD_CYCLE_POINTS(-400, "Field Cycle Points"),
    RAIDBOSS_POINTS(-500, "Raid Points");

    private final String description;
    private int clientId;

    SpecialItemType(int clientId, String description) {
        this.clientId = clientId;
        this.description = description;
    }

    public static SpecialItemType getByClientId(int clientId) {
        for (SpecialItemType type : values()) {
            if (type.getClientId() == clientId) {
                return type;
            }
        }
        return null;
    }

    public int getClientId() {
        return clientId;
    }

    public String getDescription() {
        return description;
    }
}
