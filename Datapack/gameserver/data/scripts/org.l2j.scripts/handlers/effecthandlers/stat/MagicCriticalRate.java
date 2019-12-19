package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class MagicCriticalRate extends AbstractStatEffect {

    public MagicCriticalRate(StatsSet params) {
        super(params, Stat.MAGIC_CRITICAL_RATE);
    }
}
