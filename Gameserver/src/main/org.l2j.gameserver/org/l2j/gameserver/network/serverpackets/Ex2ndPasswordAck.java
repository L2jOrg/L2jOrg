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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author mrTJO
 */
public class Ex2ndPasswordAck extends ServerPacket {
    // TODO: Enum
    public static int SUCCESS = 0x00;
    public static int WRONG_PATTERN = 0x01;
    private final int _status;
    private final int _response;

    public Ex2ndPasswordAck(int status, int response) {
        _status = status;
        _response = response;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_2ND_PASSWORD_ACK);

        writeByte((byte) _status);
        writeInt(_response == WRONG_PATTERN ? 0x01 : 0x00);
        writeInt(0x00);
    }

}
