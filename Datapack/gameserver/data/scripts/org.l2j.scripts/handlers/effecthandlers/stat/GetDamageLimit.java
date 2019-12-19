package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class GetDamageLimit extends AbstractStatAddEffect {

    public GetDamageLimit(StatsSet params) {
        super(params, Stat.DAMAGE_LIMIT);
    }
}
