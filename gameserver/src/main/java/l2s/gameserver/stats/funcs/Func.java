package l2s.gameserver.stats.funcs;

import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.stats.conditions.Condition;

/**
 * A Func object is a component of a Calculator created to manage and dynamically calculate the effect of a character property (ex : MAX_HP, REGENERATE_HP_RATE...).
 * In fact, each calculator is a table of Func object in which each Func represents a mathematic function : <BR><BR>
 *
 * FuncAtkAccuracy -> Math.sqrt(_player.getDEX())*6+_player.getLevel()<BR><BR>
 *
 * When the calc method of a calculator is launched, each mathematic function is called according to its priority <B>_order</B>.
 * Indeed, Func with lowest priority order is executed firsta and Funcs with the same order are executed in unspecified order.
 * The result of the calculation is stored in the value property of an Env class instance.<BR><BR>
 *
 */
public abstract class Func implements Comparable<Func>
{
	public static final Func[] EMPTY_FUNC_ARRAY = new Func[0];

	/** Statistics, that is affected by this function (See L2Character.CALCULATOR_XXX constants) */
	public final Stats stat;

	/**
	 * Order of functions calculation.
	 * Functions with lower order are executed first.
	 * Functions with the same order are executed in unspecified order.
	 * Usually add/substruct functions has lowest order,
	 * then bonus/penalty functions (multiplay/divide) are
	 * applied, then functions that do more complex calculations
	 * (non-linear functions).
	 */
	public final int order;

	/**
	 *  Owner can be an armor, weapon, skill, system event, quest, etc
	 *  Used to remove all functions added by this owner.
	 */
	public final Object owner;

	public final double value;

	protected Condition cond;

	public Func(Stats stat, int order, Object owner)
	{
		this(stat, order, owner, 0.);
	}

	public Func(Stats stat, int order, Object owner, double value)
	{
		this.stat = stat;
		this.order = order;
		this.owner = owner;
		this.value = value;
	}

	public void setCondition(Condition cond)
	{
		this.cond = cond;
	}

	/**
	 * Для отладки
	 */
	public Condition getCondition()
	{
		return cond;
	}

	public abstract void calc(Env env);

	@Override
	public int compareTo(Func f) throws NullPointerException
	{
		return order - f.order;
	}
}