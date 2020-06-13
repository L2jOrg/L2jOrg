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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author KenM
 */
public class ExSetCompassZoneCode extends ServerPacket {
    // TODO: Enum
    public static final int ALTEREDZONE = 0x08;
    public static final int SIEGEWARZONE1 = 0x0A;
    public static final int SIEGEWARZONE2 = 0x0B;
    public static final int PEACEZONE = 0x0C;
    public static final int SEVENSIGNSZONE = 0x0D;
    public static final int PVPZONE = 0x0E;
    public static final int GENERALZONE = 0x0F;

    private final int _zoneType;

    public ExSetCompassZoneCode(int val) {
        _zoneType = val;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SET_COMPASS_ZONE_CODE);

        writeInt(_zoneType);
    }

}
