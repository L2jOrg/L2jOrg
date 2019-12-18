package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class PveMagicalSkillDefenceBonus extends AbstractStatEffect {
	public PveMagicalSkillDefenceBonus(StatsSet params)
	{
		super(params, Stat.PVE_MAGICAL_SKILL_DEFENCE);
	}
}
