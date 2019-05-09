package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stats;

public class FishingExpSpBonus extends AbstractStatEffect {

    public  FishingExpSpBonus(StatsSet params) {
        super(params, Stats.FISHING_EXP_SP_BONUS);
    }
}
