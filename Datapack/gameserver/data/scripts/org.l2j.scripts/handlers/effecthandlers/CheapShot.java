package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class CheapShot extends AbstractEffect {
	private CheapShot() {
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.CHEAPSHOT.getMask();
	}

	public static class Factory implements SkillEffectFactory {
		private static final CheapShot INSTANCE = new CheapShot();

		@Override
		public AbstractEffect create(StatsSet data) {
			return INSTANCE;
		}

		@Override
		public String effectName() {
			return "CheapShot";
		}
	}
}