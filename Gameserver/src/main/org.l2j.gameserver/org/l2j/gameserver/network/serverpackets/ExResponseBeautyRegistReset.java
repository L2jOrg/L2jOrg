/*
 * Copyright © 2019-2021 L2JOrg
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

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author Sdw
 */
public class ExResponseBeautyRegistReset extends ServerPacket {
    public static final int FAILURE = 0;
    public static final int SUCCESS = 1;
    public static final int CHANGE = 0;
    public static final int RESTORE = 1;
    private final Player _activeChar;
    private final int _type;
    private final int _result;

    public ExResponseBeautyRegistReset(Player activeChar, int type, int result) {
        _activeChar = activeChar;
        _type = type;
        _result = result;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_RESPONSE_BEAUTY_REGIST_RESET, buffer );

        buffer.writeLong(_activeChar.getAdena());
        buffer.writeLong(_activeChar.getBeautyTickets());
        buffer.writeInt(_type);
        buffer.writeInt(_result);
        buffer.writeInt(_activeChar.getVisualHair());
        buffer.writeInt(_activeChar.getVisualFace());
        buffer.writeInt(_activeChar.getVisualHairColor());
    }

}
