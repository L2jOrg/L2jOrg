package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.engine.skill.api.Skill;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Lucky effect implementation.
 * @author Zoey76
 * @author JoeAlisson
 */
public final class Lucky extends AbstractEffect {
	private Lucky() {
	}
	
	@Override
	public boolean canStart(Creature effector, Creature effected, Skill skill)
	{
		return nonNull(effector) && isPlayer(effected);
	}

	public static class Factory implements SkillEffectFactory {
		private static final Lucky INSTANCE = new Lucky();

		@Override
		public AbstractEffect create(StatsSet data) {
			return INSTANCE;
		}

		@Override
		public String effectName() {
			return "Lucky";
		}
	}
}
