package l2s.gameserver.templates.fish;

/**
 * @author Bonux
 **/
public final class FishRewardTemplate
{
	private final int _minLevel;
	private final int _maxLevel;
	private final long _exp;
	private final long _sp;

	public FishRewardTemplate(int minLevel, int maxLevel, long exp, long sp)
	{
		_minLevel = minLevel;
		_maxLevel = maxLevel;
		_exp = exp;
		_sp = sp;
	}

	public int getMinLevel()
	{
		return _minLevel;
	}

	public int getMaxLevel()
	{
		return _maxLevel;
	}

	public long getExp()
	{
		return _exp;
	}

	public long getSp()
	{
		return _sp;
	}
}