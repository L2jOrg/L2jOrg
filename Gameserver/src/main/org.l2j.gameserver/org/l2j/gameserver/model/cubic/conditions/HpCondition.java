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
public class HpCondition implements ICubicCondition {
    private final HpConditionType _type;
    private final int _hpPer;

    public HpCondition(HpConditionType type, int hpPer) {
        _type = type;
        _hpPer = hpPer;
    }

    @Override
    public boolean test(CubicInstance cubic, Creature owner, WorldObject target) {
        if (isCreature(target)  || isDoor(target)) {
            final double hpPer = (isDoor(target) ? (Door) target : (Creature) target).getCurrentHpPercent();
            switch (_type) {
                case GREATER: {
                    return hpPer > _hpPer;
                }
                case LESSER: {
                    return hpPer < _hpPer;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " chance: " + _hpPer;
    }

    public enum HpConditionType {
        GREATER,
        LESSER;
    }
}
