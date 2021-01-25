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
package org.l2j.gameserver.network.serverpackets.raidbossinfo;

import io.github.joealisson.mmocore.WritableBuffer;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.instancemanager.BossStatus;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 * @author JoeAlisson
 */
public class ExRaidBossSpawnInfo extends ServerPacket {
    private final IntMap<BossStatus> _bossIds;

    public ExRaidBossSpawnInfo(IntMap<BossStatus> bossIds) {
        _bossIds = bossIds;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_RAID_BOSS_SPAWN_INFO, buffer );

        buffer.writeInt(_bossIds.size());
        for(var bossStatus : _bossIds.entrySet()) {
            buffer.writeInt(bossStatus.getKey());
            buffer.writeInt(bossStatus.getValue().ordinal());
            buffer.writeInt(0); // TODO death time
        }
    }

}
