package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stats;

public class SpiritXpModify extends  AbstractStatEffect {

    public SpiritXpModify(StatsSet params) {
        super(params, Stats.ELEMENTAL_SPIRIT_BONUS_XP);
    }
}
