package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.skills.Skill;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayable;

/**
 * Noblesse Blessing effect implementation.
 * @author earendil
 */
public final class NoblesseBless extends AbstractEffect {
	public NoblesseBless(StatsSet params) {
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
}
