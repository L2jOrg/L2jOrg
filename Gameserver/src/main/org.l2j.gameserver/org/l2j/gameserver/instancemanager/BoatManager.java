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
package org.l2j.gameserver.instancemanager;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.VehiclePathPoint;
import org.l2j.gameserver.model.actor.instance.Boat;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.CreatureTemplate;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.world.World;

import java.util.HashMap;
import java.util.Map;

public class BoatManager {
    public static final int TALKING_ISLAND = 1;
    public static final int GLUDIN_HARBOR = 2;
    public static final int RUNE_HARBOR = 3;
    private final Map<Integer, Boat> _boats = new HashMap<>();
    private final boolean[] _docksBusy = new boolean[3];

    private BoatManager() {
        for (int i = 0; i < _docksBusy.length; i++) {
            _docksBusy[i] = false;
        }
    }

    public Boat getNewBoat(int boatId, int x, int y, int z, int heading) {
        if (!Config.ALLOW_BOAT) {
            return null;
        }

        final StatsSet npcDat = new StatsSet();
        npcDat.set("npcId", boatId);
        npcDat.set("level", 0);
        npcDat.set("jClass", "boat");

        npcDat.set("baseSTR", 0);
        npcDat.set("baseCON", 0);
        npcDat.set("baseDEX", 0);
        npcDat.set("baseINT", 0);
        npcDat.set("baseWIT", 0);
        npcDat.set("baseMEN", 0);

        npcDat.set("baseShldDef", 0);
        npcDat.set("baseShldRate", 0);
        npcDat.set("baseAccCombat", 38);
        npcDat.set("baseEvasRate", 38);
        npcDat.set("baseCritRate", 38);

        // npcDat.set("name", "");
        npcDat.set("collision_radius", 0);
        npcDat.set("collision_height", 0);
        npcDat.set("sex", "male");
        npcDat.set("type", "");
        npcDat.set("baseAtkRange", 0);
        npcDat.set("baseMpMax", 0);
        npcDat.set("baseCpMax", 0);
        npcDat.set("rewardExp", 0);
        npcDat.set("rewardSp", 0);
        npcDat.set("basePAtk", 0);
        npcDat.set("baseMAtk", 0);
        npcDat.set("basePAtkSpd", 0);
        npcDat.set("aggroRange", 0);
        npcDat.set("baseMAtkSpd", 0);
        npcDat.set("rhand", 0);
        npcDat.set("lhand", 0);
        npcDat.set("armor", 0);
        npcDat.set("baseWalkSpd", 0);
        npcDat.set("baseRunSpd", 0);
        npcDat.set("baseHpMax", 50000);
        npcDat.set("baseHpReg", 3.e-3f);
        npcDat.set("baseMpReg", 3.e-3f);
        npcDat.set("basePDef", 100);
        npcDat.set("baseMDef", 100);
        final CreatureTemplate template = new CreatureTemplate(npcDat);
        final Boat boat = new Boat(template);
        _boats.put(boat.getObjectId(), boat);
        boat.setHeading(heading);
        boat.setXYZInvisible(x, y, z);
        boat.spawnMe();
        return boat;
    }

    /**
     * @param boatId
     * @return
     */
    public Boat getBoat(int boatId) {
        return _boats.get(boatId);
    }

    /**
     * Lock/unlock dock so only one ship can be docked
     *
     * @param h     Dock Id
     * @param value True if dock is locked
     */
    public void dockShip(int h, boolean value) {
        try {
            _docksBusy[h] = value;
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }

    /**
     * Check if dock is busy
     *
     * @param h Dock Id
     * @return Trye if dock is locked
     */
    public boolean dockBusy(int h) {
        try {
            return _docksBusy[h];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    /**
     * Broadcast one packet in both path points
     *
     * @param point1
     * @param point2
     * @param packet
     */
    public void broadcastPacket(VehiclePathPoint point1, VehiclePathPoint point2, ServerPacket packet) {
        broadcastPacketsToPlayers(point1, point2, packet);
    }

    /**
     * Broadcast several packets in both path points
     *
     * @param point1
     * @param point2
     * @param packets
     */
    public void broadcastPackets(VehiclePathPoint point1, VehiclePathPoint point2, ServerPacket... packets) {
        broadcastPacketsToPlayers(point1, point2, packets);
    }

    private void broadcastPacketsToPlayers(VehiclePathPoint point1, VehiclePathPoint point2, ServerPacket... packets) {
        for (Player player : World.getInstance().getPlayers()) {
            if (Math.hypot(player.getX() - point1.getX(), player.getY() - point1.getY()) < Config.BOAT_BROADCAST_RADIUS) {
                for (ServerPacket p : packets) {
                    player.sendPacket(p);
                }
            } else if (Math.hypot(player.getX() - point2.getX(), player.getY() - point2.getY()) < Config.BOAT_BROADCAST_RADIUS) {
                for (ServerPacket p : packets) {
                    player.sendPacket(p);
                }
            }
        }
    }

    public static BoatManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final BoatManager INSTANCE = new BoatManager();
    }
}