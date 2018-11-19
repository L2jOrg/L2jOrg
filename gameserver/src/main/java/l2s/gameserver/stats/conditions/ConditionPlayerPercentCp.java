package l2s.gameserver.stats.conditions;

import l2s.gameserver.stats.Env;

public class ConditionPlayerPercentCp extends Condition
{
	private final double _cp;

	public ConditionPlayerPercentCp(int cp)
	{
		_cp = cp / 100.;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return env.character.getCurrentCpRatio() <= _cp;
	}
}