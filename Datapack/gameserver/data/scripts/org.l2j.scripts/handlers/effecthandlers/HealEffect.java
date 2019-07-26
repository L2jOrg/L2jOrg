package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stats;

/**
 * @author Sdw
 */
public class HealEffect extends AbstractStatEffect
{
	public HealEffect(StatsSet params)
	{
		super(params, Stats.HEAL_EFFECT,  Stats.HEAL_EFFECT_ADD);
	}
}
