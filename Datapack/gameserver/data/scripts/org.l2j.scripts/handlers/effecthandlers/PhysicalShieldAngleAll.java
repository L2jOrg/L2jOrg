package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;

/**
 * @author Sdw
 */
public class PhysicalShieldAngleAll extends AbstractEffect {
	public PhysicalShieldAngleAll(StatsSet params) {
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.PHYSICAL_SHIELD_ANGLE_ALL.getMask();
	}
}
