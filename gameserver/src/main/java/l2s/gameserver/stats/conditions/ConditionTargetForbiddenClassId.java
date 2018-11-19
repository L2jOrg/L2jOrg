package l2s.gameserver.stats.conditions;

import gnu.trove.set.hash.TIntHashSet;

import l2s.gameserver.model.Creature;
import l2s.gameserver.stats.Env;

public class ConditionTargetForbiddenClassId extends Condition
{
	private TIntHashSet _classIds = new TIntHashSet();

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