package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class Accuracy extends AbstractStatEffect {

    public Accuracy(StatsSet params) {
        super(params, Stat.ACCURACY);
    }
}
