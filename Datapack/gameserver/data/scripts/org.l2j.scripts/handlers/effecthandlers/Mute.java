package handlers.effecthandlers;

import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static java.util.Objects.isNull;

/**
 * Mute effect implementation.
 *
 * @author JoeAlisson
 */
public final class Mute extends AbstractEffect {
	private Mute() {
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.MUTED.getMask();
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.MUTE;
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
		if (isNull(effected) || effected.isRaid()) {
			return;
		}

		effected.abortCast();
		effected.getAI().notifyEvent(CtrlEvent.EVT_MUTED);
	}

	public static class Factory implements SkillEffectFactory {
		private static final Mute INSTANCE = new Mute();

		@Override
		public AbstractEffect create(StatsSet data) {
			return INSTANCE;
		}

		@Override
		public String effectName() {
			return "Mute";
		}
	}
}
