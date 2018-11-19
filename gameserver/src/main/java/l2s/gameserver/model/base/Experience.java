package l2s.gameserver.model.base;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.ExperienceDataHolder;
import l2s.gameserver.templates.ExperienceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Experience
{
	private static final Logger _log = LoggerFactory.getLogger(Experience.class);

	/**
	 * Return PenaltyModifier (can use in all cases)
	 *
	 * @param count	- how many times <percents> will be substructed
	 * @param percents - percents to substruct
	 *
	 * @author Styx
	 */
	public static double penaltyModifier(long count, double percents)
	{
		return Math.max(1. - count * percents / 100, 0);
	}

	/**
	 * Максимальный достижимый уровень
	 */
	public static int getMaxLevel()
	{
		return Config.ALT_MAX_LEVEL;
	}

	/**
	 * Максимальный уровень для саба
	 */
	public static int getMaxSubLevel()
	{
		return Config.ALT_MAX_SUB_LEVEL;
	}

	public static int getMaxAvailableLevel()
	{
		return ExperienceDataHolder.getInstance().getMaxLevel();
	}

	public static int getLevel(long thisExp)
	{
		int level = 0;
		for(int i = 1; i <= getMaxAvailableLevel(); i++)
		{
			if(thisExp >= ExperienceDataHolder.getInstance().getData(i).getExp())
				level = i;
		}
		if(level == 0)
		{
			_log.warn("Cannot find level for [" + thisExp + "] experience!");
			return 1;
		}
		return level;
	}

	public static long getExpForLevel(int lvl)
	{
		ExperienceData data = ExperienceDataHolder.getInstance().getData(lvl);
		if(data == null)
			return 0;
		return data.getExp();
	}

	public static double getTrainingRate(int lvl)
	{
		ExperienceData data = ExperienceDataHolder.getInstance().getData(lvl);
		if(data == null)
			return 1;
		return data.getTrainingRate();
	}

	public static double getExpPercent(int level, long exp)
	{
		return (exp - getExpForLevel(level)) / ((getExpForLevel(level + 1) - getExpForLevel(level)) / 100.0D) * 0.01D;
	}
}