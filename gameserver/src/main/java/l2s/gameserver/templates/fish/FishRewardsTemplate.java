package l2s.gameserver.templates.fish;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bonux
 **/
public final class FishRewardsTemplate
{
	private final int _type;
	private final List<FishRewardTemplate> _rewards = new ArrayList<FishRewardTemplate>();

	public FishRewardsTemplate(int type)
	{
		_type = type;
	}

	public int getType()
	{
		return _type;
	}

	public void addReward(FishRewardTemplate reward)
	{
		_rewards.add(reward);
	}

	public List<FishRewardTemplate> getRewards()
	{
		return _rewards;
	}
}