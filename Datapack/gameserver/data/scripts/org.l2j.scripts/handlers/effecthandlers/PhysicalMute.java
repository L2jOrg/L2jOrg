package handlers.effecthandlers;

import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;

/**
 * Physical Mute effect implementation.
 * @author -Nemesiss-
 */
public final class PhysicalMute extends AbstractEffect {
	public PhysicalMute(StatsSet params) {
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
}
