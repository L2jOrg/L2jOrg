package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Sex;
import l2s.gameserver.stats.Env;

public class ConditionPlayerSex extends Condition
{
	private final Sex _sex;

	public ConditionPlayerSex(Sex sex)
	{
		_sex = sex;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return false;
		return ((Player) env.character).getSex() == _sex;
	}
}