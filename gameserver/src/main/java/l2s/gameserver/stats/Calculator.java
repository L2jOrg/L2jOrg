package l2s.gameserver.stats;

import java.util.Arrays;

import l2s.commons.lang.ArrayUtils;
import l2s.gameserver.model.Creature;
import l2s.gameserver.stats.funcs.Func;
import l2s.gameserver.stats.funcs.FuncOwner;


/**
 * A calculator is created to manage and dynamically calculate the effect of a character property (ex : MAX_HP, REGENERATE_HP_RATE...).
 * In fact, each calculator is a table of Func object in which each Func represents a mathematic function : <BR><BR>
 *
 * FuncAtkAccuracy -> Math.sqrt(_player.getDEX())*6+_player.getLevel()<BR><BR>
 *
 * When the calc method of a calculator is launched, each mathematic function is called according to its priority <B>_order</B>.
 * Indeed, Func with lowest priority order is executed firsta and Funcs with the same order are executed in unspecified order.
 * The result of the calculation is stored in the value property of an Env class instance.<BR><BR>
 *
 * Method addFunc and removeFunc permit to add and remove a Func object from a Calculator.<BR><BR>
 *
 */
public final class Calculator
{
	private Func[] _functions;

	private double _base;
	private double _last;

	public final Stats _stat;
	public final Creature _character;

	public Calculator(Stats stat, Creature character)
	{
		_stat = stat;
		_character = character;
		_functions = Func.EMPTY_FUNC_ARRAY;
	}

	/**
	 * Return the number of Funcs in the Calculator.<BR><BR>
	 */
	public int size()
	{
		return _functions.length;
	}

	/**
	 * Add a Func to the Calculator.<BR><BR>
	 */
	public void addFunc(Func f)
	{
		_functions = ArrayUtils.add(_functions, f);
		Arrays.sort(_functions);
	}

	/**
	 * Remove a Func from the Calculator.<BR><BR>
	 */
	public void removeFunc(Func f)
	{
		_functions = ArrayUtils.remove(_functions, f);
		if(_functions.length == 0)
			_functions = Func.EMPTY_FUNC_ARRAY;
		else
			Arrays.sort(_functions);
	}

	/**
	 * Remove each Func with the specified owner of the Calculator.<BR><BR>
	 */
	public void removeOwner(Object owner)
	{
		Func[] tmp = _functions;
		for(Func element : tmp)
			if(element.owner == owner)
				removeFunc(element);
	}

	/**
	 * Run each Func of the Calculator.<BR><BR>
	 */
	public void calc(Env env)
	{
		Func[] funcs = _functions;
		_base = env.value;

		boolean overrideLimits = false;
		for(Func func : funcs)
		{
			if(func == null)
				continue;

			if(func.owner instanceof FuncOwner)
			{
				if(!((FuncOwner) func.owner).isFuncEnabled())
					continue;
				if(((FuncOwner) func.owner).overrideLimits())
					overrideLimits = true;
			}
			if(func.getCondition() == null || func.getCondition().test(env))
				func.calc(env);
		}

		if(!overrideLimits)
			env.value = _stat.validate(env.value);

		if(env.value != _last)
		{
			double last = _last; //TODO [G1ta0] найти приминение в StatsChangeRecorder
			_last = env.value;
		}
	}

	/**
	 * Для отладки
	 */
	public Func[] getFunctions()
	{
		return _functions;
	}

	public double getBase()
	{
		return _base;
	}

	public double getLast()
	{
		return _last;
	}
}