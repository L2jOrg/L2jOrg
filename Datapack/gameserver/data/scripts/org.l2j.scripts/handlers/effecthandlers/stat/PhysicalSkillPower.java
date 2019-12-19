package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class PhysicalSkillPower extends AbstractStatEffect {

    public PhysicalSkillPower(StatsSet params) {
        super(params, Stat.PHYSICAL_SKILL_POWER, Stat.SKILL_POWER_ADD);
    }
}
