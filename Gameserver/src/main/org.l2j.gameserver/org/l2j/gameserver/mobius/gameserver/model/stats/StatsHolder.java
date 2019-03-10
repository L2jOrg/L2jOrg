package org.l2j.gameserver.mobius.gameserver.model.stats;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;

import java.util.function.BiPredicate;

/**
 * @author UnAfraid
 */
public class StatsHolder {
    private final Stats _stat;
    private final double _value;
    private final BiPredicate<L2Character, StatsHolder> _condition;

    public StatsHolder(Stats stat, double value, BiPredicate<L2Character, StatsHolder> condition) {
        _stat = stat;
        _value = value;
        _condition = condition;
    }

    public StatsHolder(Stats stat, double value) {
        this(stat, value, null);
    }

    public Stats getStat() {
        return _stat;
    }

    public double getValue() {
        return _value;
    }

    public boolean verifyCondition(L2Character creature) {
        return (_condition == null) || _condition.test(creature, this);
    }
}
