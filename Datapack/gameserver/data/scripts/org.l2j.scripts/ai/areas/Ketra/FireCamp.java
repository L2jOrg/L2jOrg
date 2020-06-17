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
package ai.areas.Ketra;

import ai.AbstractNpcAI;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.type.ScriptZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Thoss
 */
public class FireCamp extends AbstractNpcAI {
    private static final Logger LOGGER = LoggerFactory.getLogger(FireCamp.class);

    private static final long THREAD_LOOP_DELAY = 120000;
    private static final long RESPAWN_TIME_MIN = 10000;
    private static final long RESPAWN_TIME_MAX = 60000;
    private static final int FIRE_CAMP_LIGHTING_CHANCE = 20;
    private static final int SPAWN_CHANCE = 85;
    private static final int SPAWN_COUNT_MULT_CHANCE = 20;
    private static final int FIRE_CAMP_OFF = 18927;
    private static final int FIRE_CAMP_ON = 18928;
    private static final int KETRA_WOLF_HOUND = 21844;
    private static final int KETRA_ORC_RAIDER = 21846;

    private final Map<Integer, Npc> zoneIdNPC = new ConcurrentHashMap<>();


    private FireCamp() {
        startQuestTimer("START_KETRA_FIRE_CAMP_AI", 30000, null, null);
    }


    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        if (event.equals("START_KETRA_FIRE_CAMP_AI")) {
            AtomicInteger index = new AtomicInteger();
            getFireCampInstances().forEach(fireCampNpc -> {
                // LOGGER.info("Registering territory {} for Npc {}", "varka_camp_" + index, fireCampNpc);
                try {
                    final int zoneId = ZoneManager.getInstance().addCylinderZone(ScriptZone.class , "varka_camp_" + index, fireCampNpc.getLocation(), 70);
                    addEnterZoneId(zoneId);
                    zoneIdNPC.put(zoneId, fireCampNpc);
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                index.getAndIncrement();
            });
            startQuestTimer("KETRA_FIRE_CAMP_AI_THREAD", 10000, null, null);
        } else if (event.equals("KETRA_FIRE_CAMP_AI_THREAD")) {
            for(Map.Entry<Integer, Npc> zone: zoneIdNPC.entrySet()) {
                if(Rnd.get(100) < FIRE_CAMP_LIGHTING_CHANCE) {
                    fireCampSwitch(zone.getKey(), zone.getValue());
                }
            }
            startQuestTimer("KETRA_FIRE_CAMP_AI_THREAD", THREAD_LOOP_DELAY, null, null);
        }
        return super.onAdvEvent(event, npc, player);
    }


    /**
     * Get all the spawn of a FIRE_CAMP_OFF && FIRE_CAMP_ON.
     */
    private List<Npc> getFireCampInstances()
    {
        List<Npc> npcs = new ArrayList<>();

        World.getInstance().forEachCreature(creature -> {
            if(GameUtils.isNpc(creature) && (getAsNpc(creature).getId() == FIRE_CAMP_OFF || (getAsNpc(creature).getId() == FIRE_CAMP_ON)) ) {
                npcs.add(getAsNpc(creature));
            }
        });

        return npcs;
    }


    /**
     * Cast the arg to Npc.
     *
     * @param creature to cast.
     * @return Npc instance of creature.
     */
    public Npc getAsNpc(Creature creature) {
        return (Npc) creature;
    }


    @Override
    public String onEnterZone(Creature character, Zone zone) {
        // LOGGER.info("{} is entering zone {}", character, zone);
        final Npc fireCamp = zoneIdNPC.get(zone.getId());
        if(GameUtils.isPlayer(character) && fireCamp != null && fireCamp.getId() == FIRE_CAMP_ON && Rnd.get(100) < SPAWN_CHANCE) {
            // LOGGER.info("{} killed {} in zone {}",character, fireCamp, zone);
            fireCampKill(fireCamp, (Player) character, zone.getId());
        }
        return super.onEnterZone(character, zone);
    }


    private void fireCampSwitch(int zoneID, Npc npc) {
        // LOGGER.info("Switching {}", npc);

        final Location location = npc.getLocation();
        zoneIdNPC.remove(zoneID);
        npc.scheduleDespawn(0);

        switch (npc.getId()) {
            case FIRE_CAMP_OFF -> {
                // LOGGER.info("Spawning FIRE_CAMP_ON on {}", location);
                zoneIdNPC.put(zoneID, addSpawn(FIRE_CAMP_ON, location));
            }
            case FIRE_CAMP_ON -> {
                // LOGGER.info("Spawning FIRE_CAMP_OFF on {}", location);
                zoneIdNPC.put(zoneID, addSpawn(FIRE_CAMP_OFF, location));
            }
        }
    }


    private void fireCampKill(Npc npc, Player killer, int zoneID) {
        // LOGGER.info("{} is Killing {}", killer, npc);

        final Location location = npc.getLocation();

        switch (npc.getId()) {
            case FIRE_CAMP_OFF -> { }
            case FIRE_CAMP_ON -> {
                final long respawnDelay = Rnd.get(RESPAWN_TIME_MIN, RESPAWN_TIME_MAX);

                zoneIdNPC.remove(zoneID);
                npc.scheduleDespawn(0);

                addAttackPlayerDesire(addSpawn(KETRA_ORC_RAIDER, location), killer);
                addAttackPlayerDesire(addSpawn(KETRA_WOLF_HOUND, location), killer);

                if (Rnd.get(100) < SPAWN_COUNT_MULT_CHANCE) {
                    addAttackPlayerDesire(addSpawn(KETRA_ORC_RAIDER, location), killer);
                    addAttackPlayerDesire(addSpawn(KETRA_WOLF_HOUND, location), killer);
                }

                // LOGGER.info("Scheduling FIRE_CAMP_OFF in {}ms on {}", respawnDelay, location);
                ThreadPool.schedule(() -> {
                    // LOGGER.info("Spawning FIRE_CAMP_OFF");
                    zoneIdNPC.put(zoneID, addSpawn(FIRE_CAMP_OFF, location));
                }, respawnDelay);
            }
        }
    }


    public static AbstractNpcAI provider() {
        return new FireCamp();
    }
}