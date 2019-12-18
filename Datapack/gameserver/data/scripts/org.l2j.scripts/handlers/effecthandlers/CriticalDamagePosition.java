package handlers.effecthandlers;

import org.l2j.gameserver.enums.Position;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.util.MathUtil;

/**
 * @author Sdw
 */
public class CriticalDamagePosition extends AbstractEffect {
	private final double amount;
	private final Position position;
	
	public CriticalDamagePosition(StatsSet params) {
		amount = params.getDouble("amount", 0);
		position = params.getEnum("position", Position.class, Position.FRONT);
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item)
	{
		effected.getStats().mergePositionTypeValue(Stat.CRITICAL_DAMAGE, position, (amount / 100) + 1, MathUtil::mul);
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.getStats().mergePositionTypeValue(Stat.CRITICAL_DAMAGE, position, (amount / 100) + 1, MathUtil::div);
	}
}
