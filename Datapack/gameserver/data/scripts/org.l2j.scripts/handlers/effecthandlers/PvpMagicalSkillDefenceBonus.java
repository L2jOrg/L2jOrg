package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class PvpMagicalSkillDefenceBonus extends AbstractStatEffect {
	public PvpMagicalSkillDefenceBonus(StatsSet params)
	{
		super(params, Stat.PVP_MAGICAL_SKILL_DEFENCE);
	}
}
