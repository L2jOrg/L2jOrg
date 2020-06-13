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
package org.l2j.gameserver.model.teleporter;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.item.CommonItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author UnAfraid
 */
public class TeleportLocation extends Location {
    private final int _id;
    private final String _name;
    private final int _npcStringId;
    private final int _questZoneId;
    private final int _feeId;
    private final long _feeCount;
    private final List<Integer> _castleId;

    public TeleportLocation(int id, StatsSet set) {
        super(set);
        _id = id;
        _name = set.getString("name", null);
        _npcStringId = set.getInt("npcStringId", -1);
        _questZoneId = set.getInt("questZoneId", 0);
        _feeId = set.getInt("feeId", CommonItem.ADENA);
        _feeCount = set.getLong("feeCount", 0);

        final String castleIds = set.getString("castleId", "");
        if (castleIds.isEmpty()) {
            _castleId = Collections.emptyList();
        } else if (!castleIds.contains(";")) {
            _castleId = Collections.singletonList(Integer.parseInt(castleIds));
        } else {
            _castleId = new ArrayList<>();
            for (String castleId : castleIds.split(";")) {
                _castleId.add(Integer.parseInt(castleId));
            }
        }
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public int getNpcStringId() {
        return _npcStringId;
    }

    public int getQuestZoneId() {
        return _questZoneId;
    }

    public int getFeeId() {
        return _feeId;
    }

    public long getFeeCount() {
        return _feeCount;
    }

    public List<Integer> getCastleId() {
        return _castleId;
    }
}