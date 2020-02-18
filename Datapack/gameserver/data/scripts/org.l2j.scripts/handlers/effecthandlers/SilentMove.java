package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;

/**
 * Silent Move effect implementation.
 *
 * @author JoeAlisson
 */
public final class SilentMove extends AbstractEffect {
	private SilentMove() {
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.SILENT_MOVE.getMask();
	}

	public static class Factory implements SkillEffectFactory {

		private static final SilentMove INSTANCE = new SilentMove();

		@Override
		public AbstractEffect create(StatsSet data) {
			return INSTANCE;
		}

		@Override
		public String effectName() {
			return "SilentMove";
		}
	}
}
