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
import org.l2j.gameserver.data.database.data.TeleportBookmark;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author ShanSoft
 */
public class ExGetBookMarkInfoPacket extends ServerPacket {
    private final Player player;

    public ExGetBookMarkInfoPacket(Player cha) {
        player = cha;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_USER_BOOKMARK, buffer );

        buffer.writeInt(0x00); // Dummy
        buffer.writeInt(player.getBookMarkSlot());
        buffer.writeInt(player.getTeleportBookmarks().size());

        for (TeleportBookmark tpbm : player.getTeleportBookmarks()) {
            buffer.writeInt(tpbm.getId());
            buffer.writeInt(tpbm.getX());
            buffer.writeInt(tpbm.getY());
            buffer.writeInt(tpbm.getZ());
            buffer.writeString(tpbm.getName());
            buffer.writeInt(tpbm.getIcon());
            buffer.writeString(tpbm.getTag());
        }
    }

}
