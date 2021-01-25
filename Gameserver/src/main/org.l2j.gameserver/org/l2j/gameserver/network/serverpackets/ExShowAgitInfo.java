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
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Collection;

/**
 * @author KenM
 */
public class ExShowAgitInfo extends ServerPacket {
    public static final ExShowAgitInfo STATIC_PACKET = new ExShowAgitInfo();

    private ExShowAgitInfo() {
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_SHOW_AGIT_INFO, buffer );

        final Collection<ClanHall> clanHalls = ClanHallManager.getInstance().getClanHalls();
        buffer.writeInt(clanHalls.size());
        clanHalls.forEach(clanHall ->
        {
            buffer.writeInt(clanHall.getId());
            buffer.writeString(clanHall.getOwnerId() <= 0 ? "" : ClanTable.getInstance().getClan(clanHall.getOwnerId()).getName()); // owner clan name
            buffer.writeString(clanHall.getOwnerId() <= 0 ? "" : ClanTable.getInstance().getClan(clanHall.getOwnerId()).getLeaderName()); // leader name
            buffer.writeInt(clanHall.getType().getClientVal()); // Clan hall type
        });
    }

}
