package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instances.player.TrainingCamp;
import org.l2j.gameserver.model.base.Experience;

public class ExTrainingZone_Admission extends L2GameServerPacket
{
	private final int _timeElapsed;
	private final int _timeRemaining;
	private final double _maxExp;
	private final double _maxSp;

	public ExTrainingZone_Admission(int level, int timeElapsed, int timeRemaing)
	{
		_timeElapsed = timeElapsed;
		_timeRemaining = timeRemaing;

		double experience = Experience.getExpForLevel(level) * Experience.getTrainingRate(level) / TrainingCamp.TRAINING_DIVIDER;
		_maxExp = experience * Config.RATE_XP_BY_LVL[level];
		_maxSp = experience * Config.RATE_SP_BY_LVL[level] / 250;
	}

	public ExTrainingZone_Admission(TrainingCamp trainingCamp)
	{
		this(trainingCamp.getLevel(), 0, trainingCamp.getMaxDuration());
	}

	@Override
	public void writeImpl()
	{
		writeD(_timeElapsed);
		writeD(_timeRemaining);
		writeF(_maxExp);
		writeF(_maxSp);
	}
}