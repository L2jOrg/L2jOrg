package l2s.gameserver.stats.conditions;

import l2s.gameserver.stats.Env;

public class ConditionTargetPercentCp extends Condition
{
	private final double _cp;

	public ConditionTargetPercentCp(int cp)
	{
		_cp = cp / 100.;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return env.target != null && env.target.getCurrentCpRatio() <= _cp;
	}
}