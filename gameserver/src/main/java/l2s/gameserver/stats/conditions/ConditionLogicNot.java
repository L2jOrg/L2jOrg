package l2s.gameserver.stats.conditions;

import l2s.gameserver.stats.Env;

public class ConditionLogicNot extends Condition
{
	private final Condition _condition;

	public ConditionLogicNot(Condition condition)
	{
		_condition = condition;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return !_condition.test(env);
	}
}