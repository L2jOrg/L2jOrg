package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stats;

/**
 * @author Mobius
 */
public class SkillPowerAdd extends AbstractStatAddEffect
{
    public SkillPowerAdd(StatsSet params)
    {
        super(params, Stats.SKILL_POWER_ADD);
    }
}
