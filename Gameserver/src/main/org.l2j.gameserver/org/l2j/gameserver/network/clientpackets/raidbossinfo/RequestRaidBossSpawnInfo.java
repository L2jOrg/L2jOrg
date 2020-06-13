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
package org.l2j.gameserver.network.clientpackets.raidbossinfo;

import org.l2j.gameserver.instancemanager.DBSpawnManager;
import org.l2j.gameserver.instancemanager.GrandBossManager;
import org.l2j.gameserver.instancemanager.RaidBossStatus;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.raidbossinfo.ExRaidBossSpawnInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author THOSS
 */
public class RequestRaidBossSpawnInfo extends ClientPacket {
    private Map<Integer, Integer> _bossIds = new HashMap<Integer, Integer>();

    @Override
    public void readImpl() {
        final int count = readInt();
        for (int i = 0; i < count; i++) {
            final int bossId = readInt();
            // boss state: 1 -> alive : 0 -> dead : 2 -> in battle
            if (GrandBossManager.getInstance().getBossStatus(bossId) > -1) {
                if(GrandBossManager.getInstance().getBoss(bossId) != null && GrandBossManager.getInstance().getBoss(bossId).getAggroList().size() > 0) {
                    _bossIds.put(bossId, 2);
                } else {
                    _bossIds.put(bossId, GrandBossManager.getInstance().getBossStatus(bossId));
                }
            } else {
                if(DBSpawnManager.getInstance().isDefined(bossId) && DBSpawnManager.getInstance().getNpcs().get(bossId) != null && ((Attackable) DBSpawnManager.getInstance().getNpcs().get(bossId)).getAggroList().size() > 0) {
                    _bossIds.put(bossId, 2);
                } else {
                    _bossIds.put(bossId, DBSpawnManager.getInstance().getNpcStatusId(bossId) == RaidBossStatus.ALIVE ? 1 : 0);
                }
            }
        }
    }

    @Override
    public void runImpl() {
        client.sendPacket(new ExRaidBossSpawnInfo(_bossIds));
    }
}
