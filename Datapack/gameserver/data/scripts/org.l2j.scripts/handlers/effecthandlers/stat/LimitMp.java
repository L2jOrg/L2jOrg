package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class LimitMp extends AbstractStatEffect {

    public LimitMp(StatsSet params) {
        super(params, Stat.MAX_RECOVERABLE_MP);
    }
}
