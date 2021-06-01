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
package org.l2j.gameserver.network.clientpackets.raidbossinfo;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.instancemanager.BossManager;
import org.l2j.gameserver.instancemanager.BossStatus;
import org.l2j.gameserver.instancemanager.GrandBossManager;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.raidbossinfo.ExRaidBossSpawnInfo;

/**
 * @author THOSS
 * @author JoeAlisson
 */
public class RequestRaidBossSpawnInfo extends ClientPacket {
    private final IntMap<BossStatus> status = new HashIntMap<>();

    @Override
    public void readImpl() {
        final int count = readInt();
        for (int i = 0; i < count; i++) {
            final int bossId = readInt();
            if(GrandBossManager.getInstance().isDefined(bossId)) {
                status.put(bossId, GrandBossManager.getInstance().getBossStatus(bossId));
            } else if(BossManager.getInstance().isDefined(bossId)){
                status.put(bossId, BossManager.getInstance().getNpcStatusId(bossId));
            }
        }
    }

    @Override
    public void runImpl() {
        client.sendPacket(new ExRaidBossSpawnInfo(status));
    }
}
