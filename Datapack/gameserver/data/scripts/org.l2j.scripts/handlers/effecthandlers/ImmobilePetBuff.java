package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;
import static org.l2j.gameserver.util.GameUtils.isSummon;

/**
 * Immobile Pet Buff effect implementation.
 * @author demonia
 */
public final class ImmobilePetBuff extends AbstractEffect {
	public ImmobilePetBuff(StatsSet params) {
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.setIsImmobilized(false);
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
		if (isSummon(effected) && isPlayer(effector) && (((Summon) effected).getOwner() == effector)) {
			effected.setIsImmobilized(true);
		}
	}
}