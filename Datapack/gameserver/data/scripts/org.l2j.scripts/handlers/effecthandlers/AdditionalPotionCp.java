package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stats;

/**
 * @author Mobius
 */
public class AdditionalPotionCp extends AbstractStatAddEffect
{
    public AdditionalPotionCp(StatsSet params)
    {
        super(params, Stats.ADDITIONAL_POTION_CP);
    }
}
