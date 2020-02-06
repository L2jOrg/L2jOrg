package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class VampiricAttack extends AbstractEffect {
	public final double amount;
	public final double sum;
	
	public VampiricAttack(StatsSet params) {
		amount = params.getDouble("amount");
		sum = amount * params.getDouble("chance");
	}
	
	@Override
	public void pump(Creature effected, Skill skill) {
		effected.getStats().mergeAdd(Stat.ABSORB_DAMAGE_PERCENT, amount / 100);
		effected.getStats().addToVampiricSum(sum);
	}
}
