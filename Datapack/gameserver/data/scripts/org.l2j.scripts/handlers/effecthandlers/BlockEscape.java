package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;

/**
 * Block escape effect implementation
 * @author UnAfraid
 * @author JoeAlisson
 */
public class BlockEscape extends AbstractEffect {

	private BlockEscape() {
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.CANNOT_ESCAPE.getMask();
	}

	public static class Factory implements SkillEffectFactory {
		private static final BlockEscape INSTANCE = new BlockEscape();

		@Override
		public AbstractEffect create(StatsSet data) {
			return INSTANCE;
		}

		@Override
		public String effectName() {
			return "BlockEscape";
		}
	}
}
