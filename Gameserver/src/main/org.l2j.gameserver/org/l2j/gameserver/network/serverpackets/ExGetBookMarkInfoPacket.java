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

import org.l2j.gameserver.model.TeleportBookmark;
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
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_USER_BOOKMARK);

        writeInt(0x00); // Dummy
        writeInt(player.getBookmarkslot());
        writeInt(player.getTeleportBookmarks().size());

        for (TeleportBookmark tpbm : player.getTeleportBookmarks()) {
            writeInt(tpbm.getId());
            writeInt(tpbm.getX());
            writeInt(tpbm.getY());
            writeInt(tpbm.getZ());
            writeString(tpbm.getName());
            writeInt(tpbm.getIcon());
            writeString(tpbm.getTag());
        }
    }

}
