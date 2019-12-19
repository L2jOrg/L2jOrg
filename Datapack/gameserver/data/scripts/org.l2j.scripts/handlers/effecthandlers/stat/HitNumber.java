package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class HitNumber extends AbstractStatEffect {

    public HitNumber(StatsSet params) {
        super(params, Stat.ATTACK_COUNT_MAX);
    }
}
