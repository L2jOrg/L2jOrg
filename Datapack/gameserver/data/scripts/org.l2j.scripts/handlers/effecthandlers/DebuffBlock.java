package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;

/**
 * Effect that blocks all incoming debuffs.
 * @author Nik
 */
public final class DebuffBlock extends AbstractEffect {
	public DebuffBlock(StatsSet params) {
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.DEBUFF_BLOCK.getMask();
	}
}
