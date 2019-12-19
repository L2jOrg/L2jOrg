package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class FocusEnergy extends AbstractStatAddEffect {

    public FocusEnergy(StatsSet params) {
        super(params, Stat.MAX_MOMENTUM);
    }
}
