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

import java.time.Instant;
import java.time.ZoneOffset;

/**
 * @author JoeAlisson
 */
public class ExEnterWorld extends ServerPacket {
    private final int zoneIdOffsetSeconds;
    private final int epochInSeconds;
    private final int daylight;

    public ExEnterWorld() {
        var now = Instant.now().plusSeconds(2);
        epochInSeconds = (int) now.getEpochSecond();
        var rules = ZoneOffset.systemDefault().getRules();
        zoneIdOffsetSeconds = rules.getStandardOffset(now).getTotalSeconds();
        daylight = (int) rules.getDaylightSavings(now).toSeconds();
    }

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerExPacketId.EX_ENTER_WORLD);
        writeInt(epochInSeconds);
        writeInt(-zoneIdOffsetSeconds);
        writeInt(daylight);
        writeInt(40);
    }
}