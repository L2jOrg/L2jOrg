package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;

public class ConditionPlayerHasBuffId extends Condition
{
	private final int _id;
	private final int _level;

	public ConditionPlayerHasBuffId(int id, int level)
	{
		_id = id;
		_level = level;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		Creature character = env.character;
		if(character == null)
			return false;

		for(Abnormal effect : character.getAbnormalList())
		{
			if(effect.getSkill().getId() != _id)
				continue;

			if(_level == -1)
				return true;

			if(effect.getSkill().getLevel() >= _level)
				return true;
		}
		return false;
	}
}