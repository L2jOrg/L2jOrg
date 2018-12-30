package org.l2j.gameserver.stats.conditions;

import io.github.joealisson.primitive.sets.IntSet;
import io.github.joealisson.primitive.sets.impl.HashIntSet;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.stats.Env;

public class ConditionTargetForbiddenClassId extends Condition
{
    private IntSet _classIds = new HashIntSet();

    public ConditionTargetForbiddenClassId(String[] ids)
    {
        for(String id : ids)
            _classIds.add(Integer.parseInt(id));
    }

    @Override
    protected boolean testImpl(Env env)
    {
        Creature target = env.target;
        if(!target.isPlayable()) //why it was false? there's pve skills that didn't work
            return true;
        return !target.isPlayer() || !_classIds.contains(target.getPlayer().getActiveClassId());
    }
}