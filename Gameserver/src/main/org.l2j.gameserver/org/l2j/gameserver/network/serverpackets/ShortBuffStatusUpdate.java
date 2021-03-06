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
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ShortBuffStatusUpdate extends ServerPacket {
    public static final ShortBuffStatusUpdate RESET_SHORT_BUFF = new ShortBuffStatusUpdate(0, 0, 0, 0);

    private final int _skillId;
    private final int _skillLvl;
    private final int _skillSubLvl;
    private final int _duration;

    public ShortBuffStatusUpdate(int skillId, int skillLvl, int skillSubLvl, int duration) {
        _skillId = skillId;
        _skillLvl = skillLvl;
        _skillSubLvl = skillSubLvl;
        _duration = duration;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.SHORT_BUFF_STATUS_UPDATE, buffer );

        buffer.writeInt(_skillId);
        buffer.writeShort(_skillLvl);
        buffer.writeShort(_skillSubLvl);
        buffer.writeInt(_duration);
    }

}
