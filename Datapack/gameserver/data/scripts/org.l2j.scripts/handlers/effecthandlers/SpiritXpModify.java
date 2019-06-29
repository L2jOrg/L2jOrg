package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stats;

public class SpiritXpModify extends  AbstractStatEffect {

    public SpiritXpModify(StatsSet params) {
        super(params, Stats.BONUS_SPIRIT_XP);
    }
}
