package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;

import static org.l2j.gameserver.util.GameUtils.isNpc;
import static org.l2j.gameserver.util.GameUtils.isSummon;

/**
 * Consume Body effect implementation.
 * @author Mobius
 * @author JoeAlisson
 */
public final class ConsumeBody extends AbstractEffect {
	private ConsumeBody() {
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (!effected.isDead() || effector.getTarget() != effected || (!isNpc(effected) && !isSummon(effected)) || (isSummon(effected) && (effector != effected.getActingPlayer()))) {
			return;
		}
		
		if (isNpc(effected)) {
			((Npc) effected).endDecayTask();
		}
	}

	public static class Factory implements SkillEffectFactory {
		private static final ConsumeBody INSTANCE = new ConsumeBody();

		@Override
		public AbstractEffect create(StatsSet data) {
			return INSTANCE;
		}

		@Override
		public String effectName() {
			return "ConsumeBody";
		}
	}
}
