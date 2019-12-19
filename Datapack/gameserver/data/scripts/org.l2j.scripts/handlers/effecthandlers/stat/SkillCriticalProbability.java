package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class SkillCriticalProbability extends AbstractStatEffect {

    public SkillCriticalProbability(StatsSet params) {
        super(params, Stat.SKILL_CRITICAL_PROBABILITY);
    }
}
