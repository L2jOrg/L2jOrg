package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class MpVampiricAttack extends AbstractStatAddEffect {
	public MpVampiricAttack(StatsSet params)
	{
		super(params, Stat.ABSORB_MANA_DAMAGE_PERCENT);
	}
}
