package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class MagicalAbnormalResist extends AbstractStatAddEffect {

    public MagicalAbnormalResist(StatsSet params) {
        super(params, Stat.ABNORMAL_RESIST_MAGICAL);
    }
}
