package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class CriticalDamage extends AbstractStatEffect {

    public CriticalDamage(StatsSet params) {
        super(params, Stat.CRITICAL_DAMAGE, Stat.CRITICAL_DAMAGE_ADD);
    }
}
