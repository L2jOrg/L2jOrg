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

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author KenM
 */
public class ExPrivateStoreSetWholeMsg extends ServerPacket {
    private final int _objectId;
    private final String _msg;

    public ExPrivateStoreSetWholeMsg(Player player, String msg) {
        _objectId = player.getObjectId();
        _msg = msg;
    }

    public ExPrivateStoreSetWholeMsg(Player player) {
        this(player, player.getSellList().getTitle());
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_PRIVATE_STORE_WHOLE_MSG);

        writeInt(_objectId);
        writeString(_msg);
    }

}
