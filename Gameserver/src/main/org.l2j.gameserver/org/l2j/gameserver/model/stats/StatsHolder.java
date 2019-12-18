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
