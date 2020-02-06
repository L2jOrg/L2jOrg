package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Sdw
 */
public class RealDamage extends AbstractEffect {

	public final double power;
	
	public RealDamage(StatsSet params)
	{
		power = params.getDouble("power", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		effected.reduceCurrentHp(power, effector, skill, false, false, false, false);
		if (isPlayer(effector)) {
			effector.sendDamageMessage(effected, skill, (int) power, 0, false, false);
		}
	}
}
