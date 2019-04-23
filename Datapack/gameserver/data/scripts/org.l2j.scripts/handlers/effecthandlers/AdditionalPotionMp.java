package handlers.effecthandlers;


import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stats;

/**
 * @author Mobius
 */
public class AdditionalPotionMp extends AbstractStatAddEffect
{
    public AdditionalPotionMp(StatsSet params)
    {
        super(params, Stats.ADDITIONAL_POTION_MP);
    }
}
