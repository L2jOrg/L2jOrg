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
package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Respawn zone implementation.
 *
 * @author Nyaran
 */
public class RespawnZone extends SpawnZone {
    private final Map<Race, String> raceRespawnPoint = new HashMap<>();

    public RespawnZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature creature) {
    }

    @Override
    protected void onExit(Creature creature) {
    }

    public void addRaceRespawnPoint(String race, String point) {
        raceRespawnPoint.put(Race.valueOf(race), point);
    }

    public Map<Race, String> getAllRespawnPoints() {
        return raceRespawnPoint;
    }

    public String getRespawnPoint(Player player) {
        return raceRespawnPoint.get(player.getRace());
    }
}
