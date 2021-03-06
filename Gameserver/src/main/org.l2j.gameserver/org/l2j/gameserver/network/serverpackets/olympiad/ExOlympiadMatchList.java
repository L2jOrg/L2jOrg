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
package org.l2j.gameserver.network.serverpackets.olympiad;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.engine.olympiad.OlympiadMatch;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Collection;

/**
 * @author mrTJO
 * @author JoeAlisson
 */
public class ExOlympiadMatchList extends ServerPacket {

    private final Collection<OlympiadMatch> matches;

    public ExOlympiadMatchList(Collection<OlympiadMatch> matches) {
        this.matches = matches;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_GFX_OLYMPIAD, buffer );

        buffer.writeInt(0x00); // Type 0 = Match List, 1 = Match Result

        buffer.writeInt(matches.size());
        buffer.writeInt(0x00);

        for (var match : matches) {
            buffer.writeInt(match.getId() -1); // the client use index based starting with 1 but we are using id instead so need to decrease 1
            buffer.writeInt(match.getType().ordinal());
            buffer.writeInt(match.isInBattle() ? 0x02 : 0x01); // (0 = starting, 1 = Standby, 2 = Playing)
            buffer.writeString(match.getPlayerRedName());
            buffer.writeString(match.getPlayerBlueName());
        }
    }

}
