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
package org.l2j.gameserver.model.stats;

import org.l2j.gameserver.model.actor.Creature;

import java.util.function.BiPredicate;

/**
 * @author UnAfraid
 */
public class StatsHolder {
    private final Stat _stat;
    private final double _value;
    private final BiPredicate<Creature, StatsHolder> _condition;

    public StatsHolder(Stat stat, double value, BiPredicate<Creature, StatsHolder> condition) {
        _stat = stat;
        _value = value;
        _condition = condition;
    }

    public StatsHolder(Stat stat, double value) {
        this(stat, value, null);
    }

    public Stat getStat() {
        return _stat;
    }

    public double getValue() {
        return _value;
    }

    public boolean verifyCondition(Creature creature) {
        return (_condition == null) || _condition.test(creature, this);
    }
}
