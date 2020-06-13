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
package org.l2j.gameserver.network.authcomm.gs2as;

import io.github.joealisson.primitive.HashIntIntMap;
import io.github.joealisson.primitive.IntIntMap;
import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

public class ServerStatus extends SendablePacket {

    public static final int SERVER_LIST_STATUS = 0x01;
    public static final int SERVER_LIST_CLOCK = 0x02;
    public static final int SERVER_LIST_SQUARE_BRACKET = 0x03;
    public static final int MAX_PLAYERS = 0x04;
    public static final int TEST_SERVER = 0x05;
    public static final int SERVER_LIST_TYPE = 0x06;

    private final IntIntMap status = new HashIntIntMap();

    public ServerStatus add(int status, int value) {
        this.status.put(status, value);
        return this;
    }

    @Override
    protected void writeImpl(AuthServerClient client) {
        writeByte((byte) 0x06);
        writeInt(status.size());
        status.entrySet().forEach(entry ->  { writeInt(entry.getKey()); writeInt(entry.getValue()); });
    }
}
