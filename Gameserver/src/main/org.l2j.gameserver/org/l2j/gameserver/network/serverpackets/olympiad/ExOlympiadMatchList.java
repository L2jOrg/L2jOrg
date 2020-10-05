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
package org.l2j.gameserver.network.serverpackets.olympiad;

import org.l2j.gameserver.engine.olympiad.Olympiad;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author mrTJO
 * @author JoeAlisson
 */
public class ExOlympiadMatchList extends ServerPacket {

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_GFX_OLYMPIAD);

        var matches = Olympiad.getInstance().getMatches();

        writeInt(0x00); // Type 0 = Match List, 1 = Match Result

        writeInt(matches.size());
        writeInt(0x00);

        for (var match : matches) {
            writeInt(match.getId()); // Stadium Id (Arena 1 = 0)
            writeInt(match.getType().ordinal());
            writeInt(match.isInBattle() ? 0x02 : 0x01); // (1 = Standby, 2 = Playing)
            writeString(match.getPlayerRedName());
            writeString(match.getPlayerBlueName());
        }
    }

}
