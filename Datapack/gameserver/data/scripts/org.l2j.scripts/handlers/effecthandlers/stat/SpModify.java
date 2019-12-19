package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class SpModify extends AbstractStatEffect {

    public SpModify(StatsSet params) {
        super(params, Stat.BONUS_SP);
    }
}
