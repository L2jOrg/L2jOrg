package l2s.gameserver.stats.conditions;

import l2s.gameserver.stats.Env;

public class ConditionLogicAnd extends Condition
{
	private final static Condition[] emptyConditions = new Condition[0];

	public Condition[] _conditions = emptyConditions;

	public ConditionLogicAnd()
	{
		super();
	}

	public void add(Condition condition)
	{
		if(condition == null)
			return;

		final int len = _conditions.length;
		final Condition[] tmp = new Condition[len + 1];
		System.arraycopy(_conditions, 0, tmp, 0, len);
		tmp[len] = condition;
		_conditions = tmp;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		for(Condition c : _conditions)
			if(!c.test(env))
				return false;
		return true;
	}
}