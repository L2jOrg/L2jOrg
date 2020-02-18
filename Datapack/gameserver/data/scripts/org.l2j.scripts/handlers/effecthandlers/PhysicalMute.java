package handlers.effecthandlers;

import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * Physical Mute effect implementation.
 * @author -Nemesiss-
 * @author JoeAlisson
 */
public final class PhysicalMute extends AbstractEffect {
	private PhysicalMute() {
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.PSYCHICAL_MUTED.getMask();
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item)
	{
		effected.getAI().notifyEvent(CtrlEvent.EVT_MUTED);
	}

	public static class Factory implements SkillEffectFactory {

		private static final PhysicalMute INSTANCE = new PhysicalMute();

		@Override
		public AbstractEffect create(StatsSet data) {
			return INSTANCE;
		}

		@Override
		public String effectName() {
			return "PhysicalMute";
		}
	}
}
