package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Mobius
 */
public class BonusDropRate extends AbstractStatPercentEffect {

    public BonusDropRate(StatsSet params) {
        super(params, Stat.BONUS_DROP_RATE);
    }
}
