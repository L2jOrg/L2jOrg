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
package org.l2j.gameserver.network.serverpackets.pvpbook;

import org.l2j.gameserver.data.database.data.KillerData;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.List;

/**
 * @author JoeAlisson
 */
public class PvpBookList extends ServerPacket {

    private final List<KillerData> killers;

    public PvpBookList(List<KillerData> killers) {
        this.killers = killers;
    }

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerExPacketId.EX_PVPBOOK_LIST);
        var player = client.getPlayer();
        player.getVariables();

        writeInt(player.getRevengeUsableLocation());
        writeInt(player.getRevengeUsableTeleport());

        writeInt(killers.size());
        for (KillerData killer : killers) {
            writeSizedString(killer.getName());
            writeSizedString(killer.getClan());
            writeInt(killer.getLevel());
            writeInt(killer.getRace());
            writeInt(killer.getActiveClass());
            writeInt(killer.getKillTime());
            writeByte(killer.isOnline());
        }
    }
}
