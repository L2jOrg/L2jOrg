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
 * Special event info packet.
 *
 * @author Kerberos
 * @author mrTJO
 */
public class ExBrBroadcastEventState extends ServerPacket {
    public static final int APRIL_FOOLS = 20090401;
    public static final int EVAS_INFERNO = 20090801; // event state (0 - hide, 1 - show), day (1-14), percent (0-100)
    public static final int HALLOWEEN_EVENT = 20091031; // event state (0 - hide, 1 - show)
    public static final int RAISING_RUDOLPH = 20091225; // event state (0 - hide, 1 - show)
    public static final int LOVERS_JUBILEE = 20100214; // event state (0 - hide, 1 - show)
    private final int _eventId;
    private final int _eventState;
    private int _param0;
    private int _param1;
    private int _param2;
    private int _param3;
    private int _param4;
    private String _param5;
    private String _param6;

    public ExBrBroadcastEventState(int eventId, int eventState) {
        _eventId = eventId;
        _eventState = eventState;
    }

    public ExBrBroadcastEventState(int eventId, int eventState, int param0, int param1, int param2, int param3, int param4, String param5, String param6) {
        _eventId = eventId;
        _eventState = eventState;
        _param0 = param0;
        _param1 = param1;
        _param2 = param2;
        _param3 = param3;
        _param4 = param4;
        _param5 = param5;
        _param6 = param6;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_BR_BROADCAST_EVENT_STATE);

        writeInt(_eventId);
        writeInt(_eventState);
        writeInt(_param0);
        writeInt(_param1);
        writeInt(_param2);
        writeInt(_param3);
        writeInt(_param4);
        writeString(_param5);
        writeString(_param6);
    }

}
