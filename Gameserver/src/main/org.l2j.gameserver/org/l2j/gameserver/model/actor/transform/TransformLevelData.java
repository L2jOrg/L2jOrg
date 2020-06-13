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
package org.l2j.gameserver.model.actor.transform;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

import java.util.HashMap;
import java.util.Map;

/**
 * @author UnAfraid
 */
public final class TransformLevelData {
    private final int _level;
    private final double _levelMod;
    private Map<Integer, Double> _stats;

    public TransformLevelData(StatsSet set) {
        _level = set.getInt("val");
        _levelMod = set.getDouble("levelMod");
        addStats(Stat.MAX_HP, set.getDouble("hp"));
        addStats(Stat.MAX_MP, set.getDouble("mp"));
        addStats(Stat.MAX_CP, set.getDouble("cp"));
        addStats(Stat.REGENERATE_HP_RATE, set.getDouble("hpRegen"));
        addStats(Stat.REGENERATE_MP_RATE, set.getDouble("mpRegen"));
        addStats(Stat.REGENERATE_CP_RATE, set.getDouble("cpRegen"));
    }

    private void addStats(Stat stat, double val) {
        if (_stats == null) {
            _stats = new HashMap<>();
        }
        _stats.put(stat.ordinal(), val);
    }

    public double getStats(Stat stat, double defaultValue) {
        return _stats == null ? defaultValue : _stats.getOrDefault(stat.ordinal(), defaultValue);
    }

    public int getLevel() {
        return _level;
    }

    public double getLevelMod() {
        return _levelMod;
    }
}
