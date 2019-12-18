package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class HealEffect extends AbstractStatEffect {
	public HealEffect(StatsSet params)
	{
		super(params, Stat.HEAL_EFFECT,  Stat.HEAL_EFFECT_ADD);
	}
}
