package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author JoeAlisson
 */
public class SpiritXpModify extends AbstractStatEffect {

    public SpiritXpModify(StatsSet params) {
        super(params, Stat.ELEMENTAL_SPIRIT_BONUS_XP);
    }
}
