package l2s.gameserver.stats.conditions;

import l2s.gameserver.stats.Env;

public class ConditionPlayerPercentHp extends Condition
{
	private final double _hp;

	public ConditionPlayerPercentHp(int hp)
	{
		_hp = hp / 100.;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return env.character.getCurrentHpRatio() <= _hp;
	}
}