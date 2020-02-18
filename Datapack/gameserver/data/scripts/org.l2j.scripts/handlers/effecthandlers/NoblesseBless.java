package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.engine.skill.api.Skill;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayable;

/**
 * Noblesse Blessing effect implementation.
 * @author earendil
 * @author JoeAlisson
 */
public final class NoblesseBless extends AbstractEffect {
	private NoblesseBless() {
	}
	
	@Override
	public boolean canStart(Creature effector, Creature effected, Skill skill)
	{
		return nonNull(effector) && isPlayable(effected);
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.NOBLESS_BLESSING.getMask();
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.NOBLESSE_BLESSING;
	}

	public static class Factory implements SkillEffectFactory {

		private static final NoblesseBless INSTANCE = new NoblesseBless();

		@Override
		public AbstractEffect create(StatsSet data) {
			return INSTANCE;
		}

		@Override
		public String effectName() {
			return "NoblesseBless";
		}
	}
}
