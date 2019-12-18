package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;

/**
 * Silent Move effect implementation.
 */
public final class SilentMove extends AbstractEffect {
	public SilentMove(StatsSet params) {
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.SILENT_MOVE.getMask();
	}
}
