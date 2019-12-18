package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;

/**
 * Effect that blocks all incoming debuffs.
 * @author Nik
 */
public final class BuffBlock extends AbstractEffect {
	public BuffBlock(StatsSet params) {
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.BUFF_BLOCK.getMask();
	}
}
