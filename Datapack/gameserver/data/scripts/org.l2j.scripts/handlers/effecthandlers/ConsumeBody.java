package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isNpc;
import static org.l2j.gameserver.util.GameUtils.isSummon;

/**
 * Consume Body effect implementation.
 * @author Mobius
 */
public final class ConsumeBody extends AbstractEffect {
	public ConsumeBody(StatsSet params) {
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
}
