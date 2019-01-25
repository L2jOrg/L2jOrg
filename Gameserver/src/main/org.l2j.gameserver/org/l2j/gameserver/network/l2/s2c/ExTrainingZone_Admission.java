package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.actor.instances.player.TrainingCamp;
import org.l2j.gameserver.model.base.Experience;
import org.l2j.gameserver.settings.ServerSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

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

		var serverSettings = getSettings(ServerSettings.class);
		double experience = Experience.getExpForLevel(level) * Experience.getTrainingRate(level) / TrainingCamp.TRAINING_DIVIDER;
		_maxExp = experience * serverSettings.rateXP();
		_maxSp = experience * serverSettings.rateSP() / 250;
	}

	public ExTrainingZone_Admission(TrainingCamp trainingCamp)
	{
		this(trainingCamp.getLevel(), 0, trainingCamp.getMaxDuration());
	}

	@Override
	public void writeImpl()
	{
		writeInt(_timeElapsed);
		writeInt(_timeRemaining);
		writeDouble(_maxExp);
		writeDouble(_maxSp);
	}
}