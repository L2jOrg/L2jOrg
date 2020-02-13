package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Give SP effect implementation.
 * @author Adry_85
 */
public final class GiveSp extends AbstractEffect {
	public final int power;
	
	public GiveSp(StatsSet params)
	{
		power = params.getInt("power", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (!isPlayer(effector) || !isPlayer(effected) || effected.isAlikeDead()) {
			return;
		}
		
		effector.getActingPlayer().addExpAndSp(0, power);
	}
}