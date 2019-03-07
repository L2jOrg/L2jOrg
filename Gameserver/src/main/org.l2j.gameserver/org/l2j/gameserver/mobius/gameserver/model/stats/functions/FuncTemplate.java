package org.l2j.gameserver.mobius.gameserver.model.stats.functions;

import org.l2j.gameserver.mobius.gameserver.enums.StatFunction;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.conditions.Condition;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.model.stats.Stats;

/**
 * Function template.
 * @author mkizub, Zoey76
 */
public final class FuncTemplate
{
	private final Class<?> _functionClass;
	private final Condition _attachCond;
	private final Condition _applayCond;
	private final Stats _stat;
	private final int _order;
	private final double _value;
	
	public FuncTemplate(Condition attachCond, Condition applayCond, String functionName, int order, Stats stat, double value)
	{
		final StatFunction function = StatFunction.valueOf(functionName.toUpperCase());
		if (order >= 0)
		{
			_order = order;
		}
		else
		{
			_order = function.getOrder();
		}
		
		_attachCond = attachCond;
		_applayCond = applayCond;
		_stat = stat;
		_value = value;
		
		try
		{
			_functionClass = Class.forName("com.l2jmobius.gameserver.model.stats.functions.Func" + function.getName());
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public Class<?> getFunctionClass()
	{
		return _functionClass;
	}
	
	/**
	 * Gets the function stat.
	 * @return the stat.
	 */
	public Stats getStat()
	{
		return _stat;
	}
	
	/**
	 * Gets the function priority order.
	 * @return the order
	 */
	public int getOrder()
	{
		return _order;
	}
	
	/**
	 * Gets the function value.
	 * @return the value
	 */
	public double getValue()
	{
		return _value;
	}
	
	public boolean meetCondition(L2Character effected, Skill skill)
	{
		if ((_attachCond != null) && !_attachCond.test(effected, effected, skill))
		{
			return false;
		}
		
		if ((_applayCond != null) && !_applayCond.test(effected, effected, skill))
		{
			return false;
		}
		
		return true;
	}
}
