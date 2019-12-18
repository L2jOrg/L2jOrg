package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.Skill;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Lucky effect implementation.
 * @author Zoey76
 */
public final class Lucky extends AbstractEffect {
	public Lucky(StatsSet params) {
	}
	
	@Override
	public boolean canStart(Creature effector, Creature effected, Skill skill)
	{
		return nonNull(effector) && isPlayer(effected);
	}
}
