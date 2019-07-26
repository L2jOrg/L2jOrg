package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stats;

/**
 * @author Sdw
 */
public class SkillCriticalDamage extends AbstractStatEffect
{
	public SkillCriticalDamage(StatsSet params)
	{
		super(params, Stats.CRITICAL_DAMAGE_SKILL, Stats.CRITICAL_DAMAGE_SKILL_ADD);
	}
}
