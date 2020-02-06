package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * Physical Attack Mute effect implementation.
 * @author -Rnn-
 */
public final class PhysicalAttackMute extends AbstractEffect {

	public PhysicalAttackMute(StatsSet params) {
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
}