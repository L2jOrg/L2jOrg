package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author JoeAlisson
 */
public class FishingExpSpBonus extends AbstractStatEffect {

    public  FishingExpSpBonus(StatsSet params) {
        super(params, Stat.FISHING_EXP_SP_BONUS);
    }
}
