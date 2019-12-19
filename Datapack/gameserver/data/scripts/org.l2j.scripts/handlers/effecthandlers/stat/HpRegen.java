package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class HpRegen extends AbstractStatEffect {

    public HpRegen(StatsSet params) {
        super(params, Stat.REGENERATE_HP_RATE);
    }
}
