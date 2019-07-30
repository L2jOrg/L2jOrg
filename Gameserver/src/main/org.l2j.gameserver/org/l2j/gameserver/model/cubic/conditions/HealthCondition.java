package org.l2j.gameserver.model.cubic.conditions;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.cubic.CubicInstance;

import static org.l2j.gameserver.util.GameUtils.isCreature;
import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * @author UnAfraid
 */
public class HealthCondition implements ICubicCondition {
    private final int _min;
    private final int _max;

    public HealthCondition(int min, int max) {
        _min = min;
        _max = max;
    }

    @Override
    public boolean test(CubicInstance cubic, Creature owner, WorldObject target) {
        if (isCreature(target) || isDoor(target)) {
            final double hpPer = (isDoor(target) ? (Door) target : (Creature) target).getCurrentHpPercent();
            return (hpPer > _min) && (hpPer < _max);
        }
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " min: " + _min + " max: " + _max;
    }
}
