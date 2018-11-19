package l2s.gameserver.templates.player;

/**
 * @author Bonux
 */
public final class HpMpCpData
{
	private final double _hp;
	private final double _mp;
	private final double _cp;

	public HpMpCpData(double hp, double mp, double cp)
	{
		_hp = hp;
		_mp = mp;
		_cp = cp;
	}

	public double getHP()
	{
		return _hp;
	}

	public double getMP()
	{
		return _mp;
	}

	public double getCP()
	{
		return _cp;
	}
}