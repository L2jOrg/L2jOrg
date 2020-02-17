package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;

/**
 * Block Resurrection effect implementation.
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class BlockResurrection extends AbstractEffect {
	private BlockResurrection() {
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.BLOCK_RESURRECTION.getMask();
	}

	public static final class Factory implements SkillEffectFactory {
		private static final BlockResurrection INSTANCE = new BlockResurrection();
		@Override
		public AbstractEffect create(StatsSet data) {
			return null;
		}

		@Override
		public String effectName() {
			return "BlockResurrection";
		}
	}
}