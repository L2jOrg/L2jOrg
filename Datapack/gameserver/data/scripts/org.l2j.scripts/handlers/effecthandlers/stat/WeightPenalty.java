package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class WeightPenalty extends AbstractStatAddEffect {

    public WeightPenalty(StatsSet params) {
        super(params, Stat.WEIGHT_PENALTY);
    }
}
