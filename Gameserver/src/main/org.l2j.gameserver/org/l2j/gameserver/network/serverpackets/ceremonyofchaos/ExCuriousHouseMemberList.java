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
package org.l2j.gameserver.network.serverpackets.ceremonyofchaos;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.ceremonyofchaos.CeremonyOfChaosMember;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Collection;

/**
 * @author UnAfraid
 */
public class ExCuriousHouseMemberList extends ServerPacket {
    private final int _id;
    private final int _maxPlayers;
    private final Collection<CeremonyOfChaosMember> _players;

    public ExCuriousHouseMemberList(int id, int maxPlayers, Collection<CeremonyOfChaosMember> players) {
        _id = id;
        _maxPlayers = maxPlayers;
        _players = players;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_CURIOUS_HOUSE_MEMBER_LIST);

        writeInt(_id);
        writeInt(_maxPlayers);
        writeInt(_players.size());
        for (CeremonyOfChaosMember cocPlayer : _players) {
            final Player player = cocPlayer.getPlayer();
            writeInt(cocPlayer.getObjectId());
            writeInt(cocPlayer.getPosition());
            if (player != null) {
                writeInt(player.getMaxHp());
                writeInt(player.getMaxCp());
                writeInt((int) player.getCurrentHp());
                writeInt((int) player.getCurrentCp());
            } else {
                writeInt(0x00);
                writeInt(0x00);
                writeInt(0x00);
                writeInt(0x00);
            }
        }
    }

}
