package org.l2j.gameserver.model.stats.functions;

import org.l2j.gameserver.enums.StatFunction;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.conditions.Condition;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Stat;

/**
 * Function template.
 *
 * @author mkizub, Zoey76
 */
public final class FuncTemplate {
    private final Class<?> _functionClass;
    private final Condition _attachCond;
    private final Condition _applayCond;
    private final Stat _stat;
    private final int _order;
    private final double _value;

    public FuncTemplate(Condition attachCond, Condition applayCond, String functionName, int order, Stat stat, double value) {
        final StatFunction function = StatFunction.valueOf(functionName.toUpperCase());
        if (order >= 0) {
            _order = order;
        } else {
            _order = function.getOrder();
        }

        _attachCond = attachCond;
        _applayCond = applayCond;
        _stat = stat;
        _value = value;

        try {
            _functionClass = Class.forName("org.l2j.gameserver.model.stats.functions.Func" + function.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> getFunctionClass() {
        return _functionClass;
    }

    /**
     * Gets the function stat.
     *
     * @return the stat.
     */
    public Stat getStat() {
        return _stat;
    }

    /**
     * Gets the function priority order.
     *
     * @return the order
     */
    public int getOrder() {
        return _order;
    }

    /**
     * Gets the function value.
     *
     * @return the value
     */
    public double getValue() {
        return _value;
    }

    public boolean meetCondition(Creature effected, Skill skill) {
        if ((_attachCond != null) && !_attachCond.test(effected, effected, skill)) {
            return false;
        }

        if ((_applayCond != null) && !_applayCond.test(effected, effected, skill)) {
            return false;
        }

        return true;
    }
}
