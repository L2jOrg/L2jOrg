package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class CounterPhysicalSkill extends AbstractStatAddEffect {

    public CounterPhysicalSkill(StatsSet params) {
        super(params, Stat.VENGEANCE_SKILL_PHYSICAL_DAMAGE);
    }
}
