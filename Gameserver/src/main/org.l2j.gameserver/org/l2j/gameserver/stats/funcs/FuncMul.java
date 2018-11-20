package org.l2j.gameserver.stats.funcs;

import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.stats.Stats;

public class FuncMul extends Func
{
	public FuncMul(Stats stat, int order, Object owner, double value)
	{
		super(stat, order, owner, value);
	}

	@Override
	public void calc(Env env)
	{
		env.value *= value;
	}
}