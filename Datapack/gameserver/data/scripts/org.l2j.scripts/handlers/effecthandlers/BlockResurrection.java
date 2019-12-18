package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;

/**
 * Block Resurrection effect implementation.
 * @author UnAfraid
 */
public final class BlockResurrection extends AbstractEffect {
	public BlockResurrection(StatsSet params) {
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.BLOCK_RESURRECTION.getMask();
	}
}