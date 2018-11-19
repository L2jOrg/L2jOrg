package l2s.gameserver.model.entity.olympiad;

import l2s.gameserver.Config;

public enum CompType
{
	TEAM(0, 0, 0),
	NON_CLASSED(Config.OLYMPIAD_NONCLASSED_WINNER_REWARD_COUNT, Config.OLYMPIAD_NONCLASSED_LOOSER_REWARD_COUNT, 5),
	CLASSED(Config.OLYMPIAD_CLASSED_WINNER_REWARD_COUNT, Config.OLYMPIAD_CLASSED_LOOSER_REWARD_COUNT, 3);

	private int _winnerReward;
	private int _looserReward;
	private int _looseMult;

	private CompType(int winnerReward, int looserReward, int looseMult)
	{
		_winnerReward = winnerReward;
		_looserReward = looserReward;
		_looseMult = looseMult;
	}

	public int getWinnerReward()
	{
		return _winnerReward;
	}

	public int getLooserReward()
	{
		return _looserReward;
	}

	public int getLooseMult()
	{
		return _looseMult;
	}
}