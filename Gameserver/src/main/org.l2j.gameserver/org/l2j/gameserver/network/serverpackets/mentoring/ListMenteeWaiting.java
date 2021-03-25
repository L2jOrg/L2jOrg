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
package org.l2j.gameserver.network.serverpackets.mentoring;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * @author UnAfraid
 */
public class ListMenteeWaiting extends ServerPacket {
    private final int PLAYERS_PER_PAGE = 64;
    private final List<Player> _possibleCandiates = new ArrayList<>();
    private final int _page;

    public ListMenteeWaiting(int page, int minLevel, int maxLevel) {
        _page = page;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_MENTEE_WAITING_LIST, buffer );

        buffer.writeInt(0x01); // always 1 in retail
        if (_possibleCandiates.isEmpty()) {
            buffer.writeInt(0x00);
            buffer.writeInt(0x00);
            return;
        }

        buffer.writeInt(_possibleCandiates.size());
        buffer.writeInt(_possibleCandiates.size() % PLAYERS_PER_PAGE);

        for (Player player : _possibleCandiates) {
            if ((1 <= (PLAYERS_PER_PAGE * _page)) && (1 > (PLAYERS_PER_PAGE * (_page - 1)))) {
                buffer.writeString(player.getName());
                buffer.writeInt(player.getActiveClass());
                buffer.writeInt(player.getLevel());
            }
        }
    }

}
