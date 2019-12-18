package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;

/**
 * @author Sdw
 */
public class CheapShot extends AbstractEffect {
	public CheapShot(StatsSet params) {
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.CHEAPSHOT.getMask();
	}
}