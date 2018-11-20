package org.l2j.gameserver.skills.skillclasses;

import java.util.List;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.instances.TrapInstance;
import org.l2j.gameserver.templates.StatsSet;

public class TrapActivation extends Skill
{
	public final int _range;
	
	public TrapActivation(StatsSet set)
	{
		super(set);
		_range = set.getInteger("trapRange", 600);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
        if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
            return false;

        if(!activeChar.isPlayer())
            return false;

        List<TrapInstance> traps = activeChar.getPlayer().getTraps();
        if(traps.size() != 1)
            return false;

        if(activeChar.getDistance(traps.get(0)) > _range) //max range to cast.
            return false;

        return true;
    }

	@Override
	public void onEndCast(Creature activeChar, List<Creature> targets)
	{
		super.onEndCast(activeChar, targets);

		if(!activeChar.isPlayer())
			return;

        List<TrapInstance> traps = activeChar.getPlayer().getTraps();
        if(traps.isEmpty())
            return;

        traps.get(0).selfDestroy();
	}
}