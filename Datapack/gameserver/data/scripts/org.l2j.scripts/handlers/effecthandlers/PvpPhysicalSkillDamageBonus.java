package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class PvpPhysicalSkillDamageBonus extends AbstractStatEffect {
	public PvpPhysicalSkillDamageBonus(StatsSet params)
	{
		super(params, Stat.PVP_PHYSICAL_SKILL_DAMAGE);
	}
}
