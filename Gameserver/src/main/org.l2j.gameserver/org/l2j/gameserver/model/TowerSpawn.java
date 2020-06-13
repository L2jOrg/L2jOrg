/*
 * Copyright © 2019 L2J Mobius
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

import org.l2j.gameserver.model.interfaces.IIdentifiable;

import java.util.List;

/**
 * @author malyelfik
 */
public class TowerSpawn implements IIdentifiable {
    private final int _npcId;
    private final Location _location;
    private List<Integer> _zoneList = null;
    private int _upgradeLevel = 0;

    public TowerSpawn(int npcId, Location location) {
        _location = location;
        _npcId = npcId;
    }

    public TowerSpawn(int npcId, Location location, List<Integer> zoneList) {
        _location = location;
        _npcId = npcId;
        _zoneList = zoneList;
    }

    /**
     * Gets the NPC ID.
     *
     * @return the NPC ID
     */
    @Override
    public int getId() {
        return _npcId;
    }

    public Location getLocation() {
        return _location;
    }

    public List<Integer> getZoneList() {
        return _zoneList;
    }

    public int getUpgradeLevel() {
        return _upgradeLevel;
    }

    public void setUpgradeLevel(int level) {
        _upgradeLevel = level;
    }
}