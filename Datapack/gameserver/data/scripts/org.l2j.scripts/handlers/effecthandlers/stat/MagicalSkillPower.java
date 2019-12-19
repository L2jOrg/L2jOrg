package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Mobius
 */
public class MagicalSkillPower extends AbstractStatEffect {

    public MagicalSkillPower(StatsSet params) {
        super(params, Stat.MAGICAL_SKILL_POWER, Stat.SKILL_POWER_ADD);
    }
}