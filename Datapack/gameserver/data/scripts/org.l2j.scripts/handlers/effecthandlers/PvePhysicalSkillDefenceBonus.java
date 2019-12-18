package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class PvePhysicalSkillDefenceBonus extends AbstractStatEffect {
	public PvePhysicalSkillDefenceBonus(StatsSet params)
	{
		super(params, Stat.PVE_PHYSICAL_SKILL_DEFENCE);
	}
}
