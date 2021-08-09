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


public class ExShowSeedMapInfo extends ServerPacket {
    public static final ExShowSeedMapInfo STATIC_PACKET = new ExShowSeedMapInfo();

    private ExShowSeedMapInfo() {
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_SHOW_SEED_MAP_INFO, buffer );

        buffer.writeInt(2); // seed count

        // Seed of Destruction
        buffer.writeInt(1); // id 1? Grand Crusade
        buffer.writeInt(2770); // sys msg id + sod state

        // Seed of Infinity
        buffer.writeInt(2); // id 2? Grand Crusade
        // Manager not implemented yet
        buffer.writeInt(2766); // sys msg id
    }

}
