package l2s.gameserver.templates.fish;

/**
 * @author Bonux
 **/
public final class RodTemplate
{
	private final int _id;
	private final double _durationModifier;
	private final double _rewardModifier;
	private final int _shotConsumeCount;

	public RodTemplate(int id, double durationModifier, double rewardModifier, int shotConsumeCount)
	{
		_id = id;
		_durationModifier = durationModifier;
		_rewardModifier = rewardModifier;
		_shotConsumeCount = shotConsumeCount;
	}

	public int getId()
	{
		return _id;
	}

	public double getDurationModifier()
	{
		return _durationModifier;
	}

	public double getRewardModifier()
	{
		return _rewardModifier;
	}

	public int getShotConsumeCount()
	{
		return _shotConsumeCount;
	}
}