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
package org.l2j.gameserver.model;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneManager;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * @reworked by Thoss
 */
public class HeavenlyRift {
    public static class ClearZoneTask implements Runnable {
        private Npc _npc;

        public ClearZoneTask(Npc npc)
        {
            _npc = npc;
        }

        @Override
        public void run() {
            HeavenlyRift.getZone().forEachCreature(creature -> {
                if(GameUtils.isPlayer(creature))
                    creature.teleToLocation(114264, 13352, -5104);
                else if(GameUtils.isNpc(creature) && creature.getId() != 30401)
                    creature.decayMe();
            });

            _npc.setBusy(false);
        }
    }

    private static Zone _zone = null;

    public static Zone getZone() {
        if(_zone == null)
            _zone = ZoneManager.getInstance().getZoneByName("[heavenly_rift]");
        return _zone;
    }

    public static int getAliveNpcCount(int npcId) {
        AtomicInteger res = new AtomicInteger();

        getZone().forEachCreature(creature -> {
            res.getAndIncrement();
        }, npc -> npc.getId() == npcId && !npc.isDead());

        return res.get();
    }

    public static void startEvent20Bomb(Player player) {
        getZone().broadcastPacket(new ExShowScreenMessage(NpcStringId.SET_OFF_BOMBS_AND_GET_TREASURES, 2, 5000));

        spawnMonster(18003, 113352, 12936, 10976, 1800000);
        spawnMonster(18003, 113592, 13272, 10976, 1800000);
        spawnMonster(18003, 113816, 13592, 10976, 1800000);
        spawnMonster(18003, 113080, 13192, 10976, 1800000);
        spawnMonster(18003, 113336, 13528, 10976, 1800000);
        spawnMonster(18003, 113560, 13832, 10976, 1800000);
        spawnMonster(18003, 112776, 13512, 10976, 1800000);
        spawnMonster(18003, 113064, 13784, 10976, 1800000);
        spawnMonster(18003, 112440, 13848, 10976, 1800000);
        spawnMonster(18003, 112728, 14104, 10976, 1800000);
        spawnMonster(18003, 112760, 14600, 10976, 1800000);
        spawnMonster(18003, 112392, 14456, 10976, 1800000);
        spawnMonster(18003, 112104, 14184, 10976, 1800000);
        spawnMonster(18003, 111816, 14488, 10976, 1800000);
        spawnMonster(18003, 112104, 14760, 10976, 1800000);
        spawnMonster(18003, 112392, 15032, 10976, 1800000);
        spawnMonster(18003, 112120, 15288, 10976, 1800000);
        spawnMonster(18003, 111784, 15064, 10976, 1800000);
        spawnMonster(18003, 111480, 14824, 10976, 1800000);
        spawnMonster(18003, 113144, 14216, 10976, 1800000);
    }

    public static void startEventTower(Player player) {
        getZone().broadcastPacket(new ExShowScreenMessage(NpcStringId.PROTECT_THE_CENTRAL_TOWER_FROM_DIVINE_ANGELS, 2, 5000));
        spawnMonster(18004, 112648, 14072, 10976, 1800000);
        ThreadPool.schedule(() -> {
            for(int i = 0 ; i < 20 ; i++) {
                spawnMonster(20139, 112696, 13960, 10958, 1800000);
            }
        }, 10000);
    }

    public static void startEvent40Angels(Player player) {
        getZone().broadcastPacket(new ExShowScreenMessage(NpcStringId.DESTROY_WEAKENED_DIVINE_ANGELS, 2, 5000));
        for(int i = 0 ; i < 40 ; i++)
            spawnMonster(20139, 112696, 13960, 10958, 1800000);
    }

    private static void spawnMonster(int npcId, int x, int y, int z, long despawnTime) {
        try {
            final Spawn spawn = new Spawn(npcId);
            final Location location = new Location(x, y, z);
            spawn.setLocation(location);
            final Npc npc = spawn.doSpawn();
            npc.scheduleDespawn(despawnTime);
        } catch ( NoSuchMethodException | ClassNotFoundException e) {}
    }
}
