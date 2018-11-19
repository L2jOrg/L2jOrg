package l2s.gameserver.templates.residence;

import l2s.gameserver.Config;
import l2s.gameserver.model.base.ResidenceFunctionType;

public class ResidenceFunctionTemplate implements Comparable<ResidenceFunctionTemplate>
{
	private final int _id;
	private final ResidenceFunctionType _type;
	private final int _level;
	private final int _depth;
	private final int _period;
	private final long _cost;
	private double _hpRegen = 0.;
	private double _mpRegen = 0.;
	private double _expRestore = 0.;

	public ResidenceFunctionTemplate(int id, ResidenceFunctionType type, int level, int depth, int period, long cost)
	{
		_id = id;
		_type = type;
		_level = level;
		_depth = depth;
		_period = period;
		_cost = cost;
	}

	public int getId()
	{
		return _id;
	}

	public ResidenceFunctionType getType()
	{
		return _type;
	}

	public int getLevel()
	{
		return _level;
	}

	public int getDepth()
	{
		return _depth;
	}

	public int getPeriod()
	{
		return _period;
	}

	public long getCost()
	{
		return (long) (_cost * Config.RESIDENCE_LEASE_FUNC_MULTIPLIER);
	}

	public void setHpRegen(double value)
	{
		_hpRegen = value;
	}

	public double getHpRegen()
	{
		return _hpRegen;
	}

	public void setMpRegen(double value)
	{
		_mpRegen = value;
	}

	public double getMpRegen()
	{
		return _mpRegen;
	}

	public void setExpRestore(double value)
	{
		_expRestore = value;
	}

	public double getExpRestore()
	{
		return _expRestore;
	}

	@Override
	public int compareTo(ResidenceFunctionTemplate o)
	{
		return getLevel() - o.getLevel();
	}
}