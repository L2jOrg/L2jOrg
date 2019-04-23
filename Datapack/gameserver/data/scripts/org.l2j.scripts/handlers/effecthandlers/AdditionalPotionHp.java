package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stats;

/**
 * @author Mobius
 */
public class AdditionalPotionHp extends AbstractStatAddEffect
{
    public AdditionalPotionHp(StatsSet params)
    {
        super(params, Stats.ADDITIONAL_POTION_HP);
    }
}
