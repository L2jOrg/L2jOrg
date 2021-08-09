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
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * MyTargetSelected server packet implementation.
 *
 * @author UnAfraid
 */
public class MyTargetSelected extends ServerPacket {
    private final int _objectId;
    private final int _color;

    public MyTargetSelected(Player player, Creature target) {
        _objectId = target.getObjectId();
        _color = target.isAutoAttackable(player) ? (player.getLevel() - target.getLevel()) : 0;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.MY_TARGET_SELECTED, buffer );

        buffer.writeInt(0x01); // Grand Crusade
        buffer.writeInt(_objectId);
        buffer.writeShort(_color);
        buffer.writeInt(0x00); // Mode 0x00 - Standard; 0x03 Context Menu
    }

}
