package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class PhysicalEvasion extends AbstractConditionalHpEffect {

    public PhysicalEvasion(StatsSet params) {
        super(params, Stat.EVASION_RATE);
    }
}
