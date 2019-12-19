package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class ExpModify extends AbstractStatEffect {

    public ExpModify(StatsSet params) {
        super(params, Stat.BONUS_EXP);
    }
}
