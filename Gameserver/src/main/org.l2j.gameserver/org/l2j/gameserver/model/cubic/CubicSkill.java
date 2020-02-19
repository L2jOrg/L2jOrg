package org.l2j.gameserver.model.cubic;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.cubic.conditions.ICubicCondition;
import org.l2j.gameserver.model.holders.SkillHolder;

import java.util.ArrayList;
import java.util.List;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * @author UnAfraid
 */
public class CubicSkill extends SkillHolder implements ICubicConditionHolder {
    private final int _triggerRate;
    private final int _successRate;
    private final boolean _canUseOnStaticObjects;
    private final CubicTargetType _targetType;
    private final List<ICubicCondition> _conditions = new ArrayList<>();
    private final boolean _targetDebuff;

    public CubicSkill(StatsSet set) {
        super(set.getInt("id"), set.getInt("level"));
        _triggerRate = set.getInt("triggerRate", 100);
        _successRate = set.getInt("successRate", 100);
        _canUseOnStaticObjects = set.getBoolean("canUseOnStaticObjects", false);
        _targetType = set.getEnum("target", CubicTargetType.class, CubicTargetType.TARGET);
        _targetDebuff = set.getBoolean("targetDebuff", false);
    }

    public int getTriggerRate() {
        return _triggerRate;
    }

    public int getSuccessRate() {
        return _successRate;
    }

    public boolean canUseOnStaticObjects() {
        return _canUseOnStaticObjects;
    }

    public CubicTargetType getTargetType() {
        return _targetType;
    }

    public boolean isTargetingDebuff() {
        return _targetDebuff;
    }

    @Override
    public boolean validateConditions(CubicInstance cubic, Creature owner, WorldObject target) {
        return (!_targetDebuff || isCreature(target) && ((Creature) target).getEffectList().getDebuffCount() > 0) && (_conditions.isEmpty() || _conditions.stream().allMatch(condition -> condition.test(cubic, owner, target)));
    }

    @Override
    public void addCondition(ICubicCondition condition) {
        _conditions.add(condition);
    }

    @Override
    public String toString() {
        return "Cubic skill id: " + getSkillId() + " level: " + getLevel() + " triggerRate: " + _triggerRate + " successRate: " + _successRate + " canUseOnStaticObjects: " + _canUseOnStaticObjects + " targetType: " + _targetType + " isTargetingDebuff: " + _targetDebuff + System.lineSeparator();
    }
}
