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
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author KenM
 */
public class ExDuelEnd extends ServerPacket {
    public static final ExDuelEnd PLAYER_DUEL = new ExDuelEnd(false);
    public static final ExDuelEnd PARTY_DUEL = new ExDuelEnd(true);

    private final int _partyDuel;

    public ExDuelEnd(boolean isPartyDuel) {
        _partyDuel = isPartyDuel ? 1 : 0;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_DUEL_END, buffer );

        buffer.writeInt(_partyDuel);
    }

}
