package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stats;

/**
 * @author Sdw
 */
public class PAtk extends AbstractConditionalHpEffect
{
	public PAtk(StatsSet params)
	{
		super(params, Stats.PHYSICAL_ATTACK);
	}
}
