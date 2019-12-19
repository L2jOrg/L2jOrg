package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class PvpPhysicalSkillDefenceBonus extends AbstractStatEffect {

    public PvpPhysicalSkillDefenceBonus(StatsSet params) {
        super(params, Stat.PVP_PHYSICAL_SKILL_DEFENCE);
    }
}
