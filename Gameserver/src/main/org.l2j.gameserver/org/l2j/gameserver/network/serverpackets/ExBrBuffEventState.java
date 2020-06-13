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
 * Eva's Inferno event packet. info params: <br>
 * type (1 - %, 2 - npcId), <br>
 * value (depending on type: for type 1 - % value; for type 2 - 20573-20575), <br>
 * state (0-1), endtime (only when type 2)
 */
public class ExBrBuffEventState extends ServerPacket {
    private final int _type; // 1 - %, 2 - npcId
    private final int _value; // depending on type: for type 1 - % value; for type 2 - 20573-20575
    private final int _state; // 0-1
    private final int _endtime; // only when type 2 as unix time in seconds from 1970

    public ExBrBuffEventState(int type, int value, int state, int endtime) {
        _type = type;
        _value = value;
        _state = state;
        _endtime = endtime;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_BR_BUFF_EVENT_STATE);

        writeInt(_type);
        writeInt(_value);
        writeInt(_state);
        writeInt(_endtime);
    }

}
