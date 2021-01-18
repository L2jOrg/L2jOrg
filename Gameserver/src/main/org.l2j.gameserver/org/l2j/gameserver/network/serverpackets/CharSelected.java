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

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.world.WorldTimeController;

public class CharSelected extends ServerPacket {
    private final Player _activeChar;
    private final int _sessionId;

    public CharSelected(Player cha, int sessionId) {
        _activeChar = cha;
        _sessionId = sessionId;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.CHARACTER_SELECTED, buffer );

        buffer.writeString(_activeChar.getName());
        buffer.writeInt(_activeChar.getObjectId());
        buffer.writeString(_activeChar.getTitle());
        buffer.writeInt(_sessionId);
        buffer.writeInt(_activeChar.getClanId());
        buffer.writeInt(0x00); // ??
        buffer.writeInt(_activeChar.getAppearance().isFemale() ? 1 : 0);
        buffer.writeInt(_activeChar.getRace().ordinal());
        buffer.writeInt(_activeChar.getClassId().getId());
        buffer.writeInt(0x01); // active ??
        buffer.writeInt(_activeChar.getX());
        buffer.writeInt(_activeChar.getY());
        buffer.writeInt(_activeChar.getZ());
        buffer.writeDouble(_activeChar.getCurrentHp());
        buffer.writeDouble(_activeChar.getCurrentMp());
        buffer.writeLong(_activeChar.getSp());
        buffer.writeLong(_activeChar.getExp());
        buffer.writeInt(_activeChar.getLevel());
        buffer.writeInt(_activeChar.getReputation());
        buffer.writeInt(_activeChar.getPkKills());
        buffer.writeInt(WorldTimeController.getInstance().getGameTime() % (24 * 60)); // "reset" on 24th hour
        buffer.writeInt(0x00);
        buffer.writeInt(_activeChar.getClassId().getId());

        buffer.writeBytes(new byte[16]);

        buffer.writeInt(0x00);
        buffer.writeInt(0x00);
        buffer.writeInt(0x00);
        buffer.writeInt(0x00);

        buffer.writeInt(0x00);

        buffer.writeInt(0x00);
        buffer.writeInt(0x00);
        buffer.writeInt(0x00);
        buffer.writeInt(0x00);

        buffer.writeBytes(new byte[28]);
        buffer.writeInt(0x00);
    }

}
