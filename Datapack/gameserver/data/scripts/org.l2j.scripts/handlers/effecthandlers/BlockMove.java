package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;

/**
 * Immobile Buff effect implementation.
 * @author mkizub
 * @author JoeAlisson
 */
public final class BlockMove extends AbstractEffect {
	private BlockMove() {
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item)
	{
		effected.setIsImmobilized(true);
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.setIsImmobilized(false);
	}

	public static class Factory implements SkillEffectFactory {
		private static final BlockMove INSTANCE = new BlockMove();
		@Override
		public AbstractEffect create(StatsSet data) {
			return INSTANCE;
		}

		@Override
		public String effectName() {
			return "BlockMove";
		}
	}

}
