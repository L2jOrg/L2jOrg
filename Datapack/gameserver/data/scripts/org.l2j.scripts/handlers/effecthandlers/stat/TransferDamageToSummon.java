package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class TransferDamageToSummon extends AbstractStatAddEffect {

    public TransferDamageToSummon(StatsSet params) {
        super(params, Stat.TRANSFER_DAMAGE_SUMMON_PERCENT);
    }
}
