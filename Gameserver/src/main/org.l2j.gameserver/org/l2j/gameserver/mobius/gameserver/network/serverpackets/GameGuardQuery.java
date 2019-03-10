/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * Lets drink to code!
 *
 * @author zabbix
 */
@StaticPacket
public class GameGuardQuery extends IClientOutgoingPacket {
    public static final GameGuardQuery STATIC_PACKET = new GameGuardQuery();

    private GameGuardQuery() {

    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.GAME_GUARD_QUERY.writeId(packet);

        packet.putInt(0x27533DD9);
        packet.putInt(0x2E72A51D);
        packet.putInt(0x2017038B);
        packet.putInt(0xC35B1EA3);
    }
}
