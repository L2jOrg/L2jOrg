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
 * @author UnAfraid
 * @author  JoeAlisson
 */
public enum ShotType {
    SOULSHOTS(0),
    SPIRITSHOTS(1),
    BEAST_SOULSHOTS(2),
    BEAST_SPIRITSHOTS(3);

    private static final ShotType[] CACHED = values();

    private final int clientType;

    ShotType(int clientType) {
        this.clientType = clientType;
    }

    public static ShotType of(int type) {
        for (ShotType shotType : CACHED) {
            if(shotType.clientType == type) {
                return shotType;
            }
        }
        return null;
    }

    public int getClientType() {
        return clientType;
    }
}