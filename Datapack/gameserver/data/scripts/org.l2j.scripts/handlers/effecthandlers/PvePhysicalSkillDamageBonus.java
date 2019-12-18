package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class PvePhysicalSkillDamageBonus extends AbstractStatEffect {
	public PvePhysicalSkillDamageBonus(StatsSet params)
	{
		super(params, Stat.PVE_PHYSICAL_SKILL_DAMAGE);
	}
}
