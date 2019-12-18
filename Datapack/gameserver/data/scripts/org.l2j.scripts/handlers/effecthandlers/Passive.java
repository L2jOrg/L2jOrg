package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.skills.Skill;

import static org.l2j.gameserver.util.GameUtils.isAttackable;

/**
 * Passive effect implementation.
 * @author Adry_85
 */
public final class Passive extends AbstractEffect {
	public Passive(StatsSet params) {
	}

	@Override
	public long getEffectFlags()
	{
		return EffectFlag.PASSIVE.getMask();
	}


	@Override
	public boolean canStart(Creature effector, Creature effected, Skill skill)
	{
		return isAttackable(effected);
	}

}
