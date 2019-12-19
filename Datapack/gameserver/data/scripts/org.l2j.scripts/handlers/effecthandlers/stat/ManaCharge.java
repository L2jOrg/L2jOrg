package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class ManaCharge extends AbstractStatAddEffect {

    public ManaCharge(StatsSet params) {
        super(params, Stat.MANA_CHARGE);
    }
}
