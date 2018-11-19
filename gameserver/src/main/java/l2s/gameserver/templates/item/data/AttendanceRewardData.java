package l2s.gameserver.templates.item.data;

/**
 * @author Bonux
 */
public class AttendanceRewardData extends ItemData
{
	private final boolean _unknown;
	private final boolean _best;

	public AttendanceRewardData(int id, long count, boolean unknown, boolean best)
	{
		super(id, count);
		_unknown = unknown;
		_best = best;
	}

	public boolean isUnknown()
	{
		return _unknown;
	}

	public boolean isBest()
	{
		return _best;
	}
}