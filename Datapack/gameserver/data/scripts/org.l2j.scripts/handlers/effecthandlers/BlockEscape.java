package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;

/**
 * Block escape effect implementation
 * @author UnAfraid
 */
public class BlockEscape extends AbstractEffect {
	public BlockEscape(StatsSet params) {
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.CANNOT_ESCAPE.getMask();
	}
}
