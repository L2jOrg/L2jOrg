package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.items.instance.Item;

/**
 * Physical Attack Mute effect implementation.
 * @author -Rnn-
 * @author JoeAlisson
 */
public final class PhysicalAttackMute extends AbstractEffect {

	private PhysicalAttackMute() {
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.PSYCHICAL_ATTACK_MUTED.getMask();
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item)
	{
		effected.startPhysicalAttackMuted();
	}

	public static class Factory implements SkillEffectFactory {

		private static final PhysicalAttackMute INSTANCE = new PhysicalAttackMute();

		@Override
		public AbstractEffect create(StatsSet data) {
			return INSTANCE;
		}

		@Override
		public String effectName() {
			return "PhysicalAttackMute";
		}
	}
}