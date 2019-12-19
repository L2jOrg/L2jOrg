package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class AreaDamage extends AbstractStatAddEffect {

    public AreaDamage(StatsSet params) {
        super(params, Stat.DAMAGE_ZONE_VULN);
    }
}
