package l2s.gameserver.stats.funcs;

import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;

public class FuncAddTraitDefence extends Func
{
	public FuncAddTraitDefence(Stats stat, int order, Object owner, double value)
	{
		super(stat, order, owner, value);
	}

	@Override
	public void calc(Env env)
	{
		if(value >= 100)
			env.value = Double.POSITIVE_INFINITY;
		env.value += value;
	}
}