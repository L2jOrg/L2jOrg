package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class MpShield extends AbstractStatAddEffect {
	public MpShield(StatsSet params)
	{
		super(params, Stat.MANA_SHIELD_PERCENT);
	}
}
